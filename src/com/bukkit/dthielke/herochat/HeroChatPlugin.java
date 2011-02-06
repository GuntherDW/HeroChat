package com.bukkit.dthielke.herochat;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

import com.bukkit.dthielke.herochat.command.AutoJoinCommand;
import com.bukkit.dthielke.herochat.command.BanCommand;
import com.bukkit.dthielke.herochat.command.ChannelsCommand;
import com.bukkit.dthielke.herochat.command.Command;
import com.bukkit.dthielke.herochat.command.CreateCommand;
import com.bukkit.dthielke.herochat.command.FocusCommand;
import com.bukkit.dthielke.herochat.command.HelpCommand;
import com.bukkit.dthielke.herochat.command.IgnoreCommand;
import com.bukkit.dthielke.herochat.command.JoinCommand;
import com.bukkit.dthielke.herochat.command.KickCommand;
import com.bukkit.dthielke.herochat.command.LeaveCommand;
import com.bukkit.dthielke.herochat.command.ListCommand;
import com.bukkit.dthielke.herochat.command.ModCommand;
import com.bukkit.dthielke.herochat.command.QuickMsgCommand;
import com.bukkit.dthielke.herochat.command.ReloadCommand;
import com.bukkit.dthielke.herochat.command.RemoveCommand;
import com.bukkit.dthielke.herochat.util.ConfigurationHandler;
import com.nijikokun.bukkit.Permissions.Permissions;
import com.nijikokun.bukkit.iChat.iChat;

public class HeroChatPlugin extends JavaPlugin {

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

        private final String format;

        ChatColor(String format) {
            this.format = format;
        }

        public String format() {
            return format;
        }
    }

    public enum PluginPermission {
        ADMIN,
        CREATE,
        REMOVE
    };

    public static final String[] RESERVED_NAMES = { "ban",
                                                   "channels",
                                                   "create",
                                                   "ignore",
                                                   "help",
                                                   "ch",
                                                   "join",
                                                   "kick",
                                                   "leave",
                                                   "list",
                                                   "mod",
                                                   "remove" };

    private HeroChatPlayerListener playerListener;

    private boolean usingPermissions;

    private List<Command> commands;
    private List<Channel> channels;

    private Channel defaultChannel;

    private HashMap<Player, Channel> activeChannelMap;
    private HashMap<Player, List<String>> ignoreMap;
    private HashMap<String, List<String>> autoJoinMap;

    private Logger logger;

    private iChat iChatPlugin;

    public HeroChatPlugin(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
    }

    public String censor(String msg) {
        if (iChatPlugin == null)
            return msg;

        String censored = iChatPlugin.censored(msg);
        return censored.replaceAll("&", "\u00a7");
    }

    public boolean checkPlayerAutoJoinChannel(String player, Channel channel) {
        List<String> autojoins = autoJoinMap.get(player);

        if (autojoins == null)
            return false;

        for (String s : autojoins)
            if (s.equalsIgnoreCase(channel.getName()) || s.equalsIgnoreCase(channel.getNick()))
                return true;

        return false;
    }

    public void createIgnoreList(Player player) {
        ignoreMap.put(player, new ArrayList<String>());
    }

    public Channel getActiveChannel(Player player) {
        return activeChannelMap.get(player);
    }

    public HashMap<Player, Channel> getActiveChannelMap() {
        return activeChannelMap;
    }

    public HashMap<String, List<String>> getAutoJoinMap() {
        return autoJoinMap;
    }

    public Channel getChannel(String identifier) {
        for (Channel c : channels) {
            if (c.getName().equalsIgnoreCase(identifier) || c.getNick().equalsIgnoreCase(identifier)) {
                return c;
            }
        }

        return null;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public List<Command> getCommands() {
        return commands;
    }

    public Channel getDefaultChannel() {
        return defaultChannel;
    }

    public String getHealthBar(Player player) {
        if (iChatPlugin == null)
            return "";

        return iChatPlugin.healthBar(player, false);
    }

    public List<String> getIgnoreList(Player player) {
        List<String> ignoreList = ignoreMap.get(player);

        if (ignoreList == null)
            ignoreList = new ArrayList<String>();

        return ignoreList;
    }

    public HashMap<Player, List<String>> getIgnoreMap() {
        return ignoreMap;
    }

    public boolean hasPermission(Player player, PluginPermission permission) {
        return Permissions.Security.permission(player, "herochat." + permission.toString().toLowerCase());
    }

    public boolean hasPermission(String name, PluginPermission permission) {
        Player p = getServer().getPlayer(name);

        if (p == null || !usingPermissions)
            return false;

        return hasPermission(p, permission);
    }

    public boolean isUsingPermissions() {
        return usingPermissions;
    }

    public void joinAllDefaultChannels(boolean notifyUser) {

        Player[] players = getServer().getOnlinePlayers();

        for (Channel c : channels) {
            List<String> whitelist = c.getWhiteList();

            for (Player p : players) {

                if (c.isAutomaticallyJoined() || checkPlayerAutoJoinChannel(p.getName(), c)) {

                    if (usingPermissions && !whitelist.isEmpty()) {
                        String group = Permissions.Security.getGroup(p.getName());

                        if (whitelist.contains(group)) {
                            c.addPlayer(p);

                            if (notifyUser)
                                p.sendMessage("HeroChat: Joined channel " + c.getColoredName());
                        }
                    } else {
                        c.addPlayer(p);

                        if (notifyUser)
                            p.sendMessage("HeroChat: Joined channel " + c.getColoredName());
                    }
                }
            }
        }
    }

    public void loadConfig() {
        File file = new File(getDataFolder(), "data.yml");
        ConfigurationHandler.load(this, file);
    }

    public void log(String log) {
        logger.log(Level.INFO, "[HEROCHAT] " + log);
    }

    @Override
    public void onDisable() {
        PluginDescriptionFile desc = getDescription();
        System.out.println(desc.getName() + " version " + desc.getVersion() + " disabled.");
    }

    @Override
    public void onEnable() {
        registerEvents();
        registerCommands();

        setupPermissions();

        File file = new File(getDataFolder(), "data.yml");
        ConfigurationHandler.load(this, file);

        ignoreMap = new HashMap<Player, List<String>>();
        activeChannelMap = new HashMap<Player, Channel>();

        logger = Logger.getLogger("Minecraft");

        PluginDescriptionFile desc = getDescription();
        logger.log(Level.INFO, desc.getName() + " version " + desc.getVersion() + " enabled.");

        Plugin iChatTest = this.getServer().getPluginManager().getPlugin("iChat");

        if (iChatTest != null)
            iChatPlugin = (com.nijikokun.bukkit.iChat.iChat) iChatTest;
        else
            iChatPlugin = null;

        getServer().getPluginManager().enablePlugin(getServer().getPluginManager().getPlugin("Permissions"));

        joinAllDefaultChannels(false);
    }

    private void registerCommands() {
        commands = new ArrayList<Command>();

        commands.add(new FocusCommand(this));
        commands.add(new JoinCommand(this));
        commands.add(new LeaveCommand(this));
        commands.add(new QuickMsgCommand(this));
        commands.add(new ListCommand(this));
        commands.add(new ChannelsCommand(this));
        commands.add(new IgnoreCommand(this));
        commands.add(new CreateCommand(this));
        commands.add(new RemoveCommand(this));
        commands.add(new KickCommand(this));
        commands.add(new BanCommand(this));
        commands.add(new HelpCommand(this));
        commands.add(new ReloadCommand(this));
        commands.add(new AutoJoinCommand(this));
        commands.add(new ModCommand(this));
    }

    private void registerEvents() {
        playerListener = new HeroChatPlayerListener(this);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Normal, this);
    }

    public void saveConfig() {
        File file = new File(getDataFolder(), "data.yml");
        ConfigurationHandler.save(this, file);
    }

    public void setActiveChannel(Player player, Channel channel) {
        activeChannelMap.put(player, channel);
    }

    public void setAutoJoinMap(HashMap<String, List<String>> autoJoinMap) {
        this.autoJoinMap = autoJoinMap;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public void setDefaultChannel(Channel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public void setupPermissions() {
        Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

        if (test != null)
            usingPermissions = true;
        else
            usingPermissions = false;

    }

    // TODO: add option to broadcast player joins on a per-channel basis
    // TODO: give players a way to remove server-defined autojoins
    
    public boolean toggleAutoJoin(String player, Channel channel) {
        List<String> autojoins = autoJoinMap.get(player);

        if (autojoins == null)
            autojoins = new ArrayList<String>();

        for (String s : autojoins) {
            if (s.equalsIgnoreCase(channel.getName()) || s.equalsIgnoreCase(channel.getNick())) {
                autojoins.remove(s);
                autoJoinMap.put(player, autojoins);
                return false;
            }
        }

        autojoins.add(channel.getNick());
        autoJoinMap.put(player, autojoins);
        return true;
    }

}
