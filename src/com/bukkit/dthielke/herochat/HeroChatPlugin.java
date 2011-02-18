package com.bukkit.dthielke.herochat;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.Event;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.PluginLoader;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.util.config.Configuration;

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
import com.bukkit.dthielke.herochat.util.MessageFormatter;
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

    public static final String[] RESERVED_NAMES = { "ban", "channels", "create", "ignore", "help", "ch", "join", "kick", "leave", "list", "mod", "remove" };

    private HeroChatPlayerListener playerListener;

    private boolean usingPermissions;

    private List<Command> commands;
    private List<Channel> channels;

    private Channel defaultChannel;

    private HashMap<String, String> activeChannels = new HashMap<String, String>();
    private HashMap<Player, List<String>> ignoreMap = new HashMap<Player, List<String>>();
    private HashMap<String, List<String>> joinedChannels = new HashMap<String, List<String>>();

    private Logger logger;

    private iChat iChatPlugin;
    
    private Configuration usersConfig;
    
    public HeroChatPlugin(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
    }

    public String censor(String msg) {
        if (iChatPlugin == null)
            return msg;

        String censored = iChatPlugin.censored(msg);
        return censored.replaceAll("&", "\u00a7");
    }

    public void checkNewPlayerSettings(Player player) {
        String name = player.getName();

        if (joinedChannels.get(name) == null || joinedChannels.get(name).isEmpty()) {
            List<String> joined = new ArrayList<String>();

            for (Channel c : channels) {
                if (c.isAutomaticallyJoined()) {
                    if (usingPermissions && !c.getWhiteList().isEmpty()) {
                        String group = Permissions.Security.getGroup(name);

                        if (!c.getWhiteList().contains(group)) {
                            continue;
                        }
                    }
                    
                    joined.add(c.getName());
                }
            }

            joinedChannels.put(name, joined);
        }

        if (activeChannels.get(name) == null) {
            activeChannels.put(name, defaultChannel.getName());
        }
    }

    public void createIgnoreList(Player player) {
        ignoreMap.put(player, new ArrayList<String>());
    }

    public Channel getActiveChannel(Player player) {
        String name = player.getName();
        return getChannel(activeChannels.get(name));
    }

    public HashMap<String, String> getActiveChannels() {
        return activeChannels;
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

    public List<Channel> getJoinedChannels(Player player) {
        String name = player.getName();
        List<Channel> joined = new ArrayList<Channel>();

        for (String s : joinedChannels.get(name)) {
            Channel c = getChannel(s);
            if (c != null)
                joined.add(c);
        }

        return joined;
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

    public void joinChannel(Player player, Channel channel) {
        String name = player.getName();

        List<String> channels = joinedChannels.get(name);
        if (!channels.contains(channel.getName())) {
            joinedChannels.get(name).add(channel.getName());
            //savePlayerSettings(name);
        }
    }

    public void leaveChannel(Player player, Channel channel) {
        String name = player.getName();
        joinedChannels.get(name).remove(channel.getName());
        //savePlayerSettings(name);
    }

    public void loadConfig() {
        Configuration config = this.getConfiguration();
        config.load();

        channels = new ArrayList<Channel>();
        for (String s : config.getKeys("channels")) {
            String root = "channels." + s + ".";
            Channel c;
            if (config.getBoolean(root + "options.local", false))
                c = new LocalChannel(this);
            else
                c = new Channel(this);

            c.setName(s);

            c.setNick(config.getString(root + "nickname", "DEFAULT-NICK"));
            c.setColor(ChatColor.valueOf(config.getString(root + "color", "WHITE")));
            c.setFormatter(new MessageFormatter(config.getString(root + "message-format", "{default}")));

            String options = root + "options.";
            c.setJoinMessages(config.getBoolean(options + "join-messages", true));
            c.setQuickMessagable(config.getBoolean(options + "shortcut-allowed", false));
            c.setPermanent(config.getBoolean(options + "permanent", false));
            c.setHidden(config.getBoolean(options + "hidden", false));
            c.setAutomaticallyJoined(config.getBoolean(options + "auto-join", false));
            c.setForced(config.getBoolean(options + "forced", false));

            String lists = root + "lists.";
            c.setBanList(config.getStringList(lists + "bans", null));
            c.setModerators(config.getStringList(lists + "moderators", null));

            String permissions = root + "permissions.";
            c.setWhiteList(config.getStringList(permissions + "join", null));
            c.setVoiceList(config.getStringList(permissions + "speak", null));

            channels.add(c);
        }

        String globals = "globals.";
        defaultChannel = getChannel(config.getString(globals + "default-channel", channels.get(0).getName()));
        MessageFormatter.setDefaultMessageFormat(config.getString(globals + "default-message-format", "{name}: "));
        LocalChannel.setDistance(config.getInt(globals + "default-local-distance", 100));

        loadPlayerSettings();
    }

    public void loadConfigOld() {
        File file = new File(getDataFolder(), "data.yml");
        ConfigurationHandler.load(this, file);
    }

    public void loadPlayerSettings() {
        if (usersConfig.getNode("users") == null)
            return;
        
        for (String name : usersConfig.getKeys("users"))
            loadPlayerSettings(name);
    }

    public void loadPlayerSettings(String name) {
        activeChannels.put(name, usersConfig.getString("users." + name + ".active-channel", defaultChannel.getName()));
        joinedChannels.put(name, usersConfig.getStringList("users." + name + ".joined-channels", null));
    }

    public void log(String log) {
        logger.log(Level.INFO, "[HEROCHAT] " + log);
    }

    @Override
    public void onDisable() {
        saveConfig();
        
        this.activeChannels.clear();
        this.channels.clear();
        this.commands.clear();
        this.ignoreMap.clear();
        this.joinedChannels.clear();
        
        this.defaultChannel = null;
        this.iChatPlugin = null;
        this.logger = null;
        this.playerListener = null;
        this.usersConfig = null;
        this.usingPermissions = false;
        
        PluginDescriptionFile desc = getDescription();
        System.out.println(desc.getName() + " version " + desc.getVersion() + " disabled.");
    }

    @Override
    public void onEnable() {
        usersConfig = new Configuration(new File(getDataFolder(), "users.yml"));
        usersConfig.load();
        
        registerEvents();
        registerCommands();

        setupPermissions();
        pickLoader();

        logger = Logger.getLogger("Minecraft");

        PluginDescriptionFile desc = getDescription();
        logger.log(Level.INFO, desc.getName() + " version " + desc.getVersion() + " enabled.");

        Plugin iChatTest = this.getServer().getPluginManager().getPlugin("iChat");

        if (iChatTest != null)
            iChatPlugin = (com.nijikokun.bukkit.iChat.iChat) iChatTest;
        else
            iChatPlugin = null;

        getServer().getPluginManager().enablePlugin(getServer().getPluginManager().getPlugin("Permissions"));
        
        for (Player p : getServer().getOnlinePlayers())
            playerListener.onPlayerJoin(new PlayerEvent(Type.PLAYER_JOIN, p));
    }
    
    public void reload() {
        usersConfig.load();
        pickLoader();
        
        for (Player p : getServer().getOnlinePlayers())
            playerListener.onPlayerJoin(new PlayerEvent(Type.PLAYER_JOIN, p));
    }

    public void pickLoader() {
        boolean old = true;
        File file = new File(getDataFolder(), "data.yml");
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));
            old = reader.readLine().trim().equals("!<configuration>");
            reader.close();
        } catch (Exception e) {
            old = false;
        }

        if (old) {
            loadConfigOld();
            try {
                BufferedWriter writer = new BufferedWriter(new FileWriter(file, false));
                writer.write("This file can be deleted. See config.yml for configuration options.");
                writer.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        } else {
            loadConfig();
        }
        saveConfig();
    }

    public void saveConfig() {
        Configuration config = this.getConfiguration();

        String globals = "globals.";
        config.setProperty(globals + "default-channel", defaultChannel.getName());
        config.setProperty(globals + "default-message-format", MessageFormatter.getDefaultMessageFormat());
        config.setProperty(globals + "default-local-distance", LocalChannel.getDistance());

        for (Channel c : channels) {
            if (!c.isSaved())
                continue;
            
            String root = "channels." + c.getName() + ".";
            config.setProperty(root + "nickname", c.getNick());
            config.setProperty(root + "color", c.getColor().toString());
            config.setProperty(root + "message-format", c.getFormatter().getFormat());

            String options = root + "options.";
            config.setProperty(options + "join-messages", c.isJoinMessages());
            config.setProperty(options + "shortcut-allowed", c.isQuickMessagable());
            config.setProperty(options + "permanent", c.isPermanent());
            config.setProperty(options + "hidden", c.isHidden());
            config.setProperty(options + "auto-join", c.isAutomaticallyJoined());
            config.setProperty(options + "local", c instanceof LocalChannel);
            config.setProperty(options + "forced", c.isForced());

            String lists = root + "lists.";
            config.setProperty(lists + "bans", c.getBanList());
            config.setProperty(lists + "moderators", c.getModerators());

            String permissions = root + "permissions.";
            config.setProperty(permissions + "join", c.getWhiteList());
            config.setProperty(permissions + "speak", c.getVoiceList());
        }

        config.save();

        savePlayerSettings();
    }

    public void saveConfigOld() {
        File file = new File(getDataFolder(), "data.yml");
        ConfigurationHandler.save(this, file);
    }

    public void savePlayerSettings() {
        for (String name : activeChannels.keySet())
            savePlayerSettings(name);
    }

    public void savePlayerSettings(String name) {
        usersConfig.setProperty("users." + name + ".active-channel", activeChannels.get(name));
        usersConfig.setProperty("users." + name + ".joined-channels", joinedChannels.get(name));
        
        usersConfig.save();
    }

    public void setActiveChannel(Player player, Channel channel) {
        String name = player.getName();
        activeChannels.put(name, channel.getName());
        //savePlayerSettings(name);

        channel.addPlayer(player);
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
        // commands.add(new AutoJoinCommand(this));
        commands.add(new ModCommand(this));
        // commands.add(new DefaultCommand(this));
    }

    private void registerEvents() {
        playerListener = new HeroChatPlayerListener(this);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Normal, this);
    }
}
