package com.herocraftonline.dthielke.herochat;

import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.herocraftonline.dthielke.herochat.channels.ChannelManager;
import com.herocraftonline.dthielke.herochat.command.CommandManager;
import com.herocraftonline.dthielke.herochat.command.commands.BanCommand;
import com.herocraftonline.dthielke.herochat.command.commands.CreateCommand;
import com.herocraftonline.dthielke.herochat.command.commands.FocusCommand;
import com.herocraftonline.dthielke.herochat.command.commands.HelpCommand;
import com.herocraftonline.dthielke.herochat.command.commands.IgnoreCommand;
import com.herocraftonline.dthielke.herochat.command.commands.JoinCommand;
import com.herocraftonline.dthielke.herochat.command.commands.KickCommand;
import com.herocraftonline.dthielke.herochat.command.commands.LeaveCommand;
import com.herocraftonline.dthielke.herochat.command.commands.ListCommand;
import com.herocraftonline.dthielke.herochat.command.commands.ModCommand;
import com.herocraftonline.dthielke.herochat.command.commands.QuickMsgCommand;
import com.herocraftonline.dthielke.herochat.command.commands.ReloadCommand;
import com.herocraftonline.dthielke.herochat.command.commands.RemoveCommand;
import com.herocraftonline.dthielke.herochat.command.commands.WhoCommand;
import com.herocraftonline.dthielke.herochat.util.ConfigManager;
import com.herocraftonline.dthielke.herochat.util.PermissionHelper;
import com.nijiko.permissions.PermissionHandler;
import com.nijikokun.bukkit.Permissions.Permissions;

public class HeroChat extends JavaPlugin {

    public enum ChatColor {
        BLACK("§0"),
        NAVY("§1"),
        GREEN("§2"),
        BLUE("§3"),
        RED("§4"),
        PURPLE("§5"),
        GOLD("§6"),
        LIGHT_GRAY("§7"),
        GRAY("§8"),
        DARK_PURPLE("§9"),
        LIGHT_GREEN("§a"),
        LIGHT_BLUE("§b"),
        ROSE("§c"),
        LIGHT_PURPLE("§d"),
        YELLOW("§e"),
        WHITE("§f");

        public final String str;

        ChatColor(String str) {
            this.str = str;
        }
    }

    private Logger log = Logger.getLogger("Minecraft");
    private ChannelManager channelManager;
    private CommandManager commandManager;
    private ConfigManager configManager;
    private PermissionHelper permissions;
    private String tag;
    private HeroChatPlayerListener playerListener;

    @Override
    public void onDisable() {
        for (Player p : getServer().getOnlinePlayers()) {
            configManager.savePlayer(p.getName());
            configManager.save();
        }
    }

    @Override
    public void onEnable() {
        channelManager = new ChannelManager(this);
        permissions = loadPermissions();
        if (permissions == null) {
            return;
        }

        registerEvents();
        registerCommands();
        
        PluginDescriptionFile desc = getDescription();
        log(desc.getName() + " version " + desc.getVersion() + " enabled.");

        try {
            configManager = new ConfigManager(this);
            configManager.load();
            for (Player p : getServer().getOnlinePlayers()) {
                playerListener.onPlayerJoin(new PlayerEvent(Event.Type.PLAYER_JOIN, p));
            }
            configManager.save();
        } catch (Exception e) {
            log("Error encountered while loading settings.");
            e.printStackTrace();
            setEnabled(false);
            return;
        }
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        return commandManager.dispatch(sender, command, label, args);
    }

    private void registerEvents() {
        playerListener = new HeroChatPlayerListener(this);
        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.Low, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_COMMAND_PREPROCESS, playerListener, Event.Priority.Normal, this);
    }

    private void registerCommands() {
        commandManager = new CommandManager();
        commandManager.addCommand(new HelpCommand(this));
        commandManager.addCommand(new BanCommand(this));
        commandManager.addCommand(new ListCommand(this));
        commandManager.addCommand(new CreateCommand(this));
        commandManager.addCommand(new FocusCommand(this));
        commandManager.addCommand(new IgnoreCommand(this));
        commandManager.addCommand(new JoinCommand(this));
        commandManager.addCommand(new KickCommand(this));
        commandManager.addCommand(new LeaveCommand(this));
        commandManager.addCommand(new WhoCommand(this));
        commandManager.addCommand(new ModCommand(this));
        commandManager.addCommand(new QuickMsgCommand(this));
        commandManager.addCommand(new ReloadCommand(this));
        commandManager.addCommand(new RemoveCommand(this));
    }

    private PermissionHelper loadPermissions() {
        Plugin p = this.getServer().getPluginManager().getPlugin("Permissions");
        if (p != null) {
            Permissions permissions = (Permissions) p;
            if (!permissions.isEnabled()) {
                this.getServer().getPluginManager().enablePlugin(permissions);
            }
            boolean upToDate = true;
            String version = permissions.getDescription().getVersion();
            String[] split = permissions.getDescription().getVersion().split(".");
            try {
                for (int i = 0; i < split.length; i++) {
                    int v = Integer.parseInt(split[i]);
                    if (v < PermissionHelper.MIN_VERSION[i]) {
                        upToDate = false;
                    }
                }
            } catch (NumberFormatException e) {
                upToDate = false;
            }
            if (upToDate) {
                PermissionHandler security = permissions.getHandler();
                security.load();
                PermissionHelper ph = new PermissionHelper(security);
                log("Permissions " + version + " found.");
                return ph;
            }
        }

        log("Permissions 2.4 not found! Disabling HeroChat.");
        this.getPluginLoader().disablePlugin(this);
        return null;
    }

    public void log(String msg) {
        log.log(Level.INFO, "[HeroChat] " + msg);
    }

    public ChannelManager getChannelManager() {
        return channelManager;
    }

    public CommandManager getCommandManager() {
        return commandManager;
    }

    public PermissionHelper getPermissions() {
        return permissions;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getTag() {
        return tag;
    }

    public ConfigManager getConfigManager() {
        return configManager;
    }

}
