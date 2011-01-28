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
import com.bukkit.dthielke.herochat.command.QuickMsgCommand;
import com.bukkit.dthielke.herochat.command.ReloadCommand;
import com.bukkit.dthielke.herochat.command.RemoveCommand;
import com.bukkit.dthielke.herochat.util.Configuration;
import com.bukkit.dthielke.herochat.util.MessageFormatter;
import com.bukkit.dthielke.herochat.util.Configuration.ChannelWrapper;
import com.bukkit.dthielke.herochat.util.Configuration.ChannelWrapper.ChannelProperties;
import com.nijikokun.bukkit.Permissions.Permissions;

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

    private MessageFormatter regMsgFormatter;
    private MessageFormatter localMsgFormatter;

    private Logger logger;
    
    public HeroChatPlugin(PluginLoader pluginLoader, Server instance, PluginDescriptionFile desc, File folder, File plugin, ClassLoader cLoader) {
        super(pluginLoader, instance, desc, folder, plugin, cLoader);
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

        loadConfig();

        ignoreMap = new HashMap<Player, List<String>>();
        activeChannelMap = new HashMap<Player, Channel>();
        
        logger = Logger.getLogger("Minecraft");
        
        PluginDescriptionFile desc = getDescription();
        logger.log(Level.INFO, desc.getName() + " version " + desc.getVersion() + " enabled.");
    }

    private void registerEvents() {
        playerListener = new HeroChatPlayerListener(this);

        PluginManager pm = getServer().getPluginManager();
        pm.registerEvent(Event.Type.PLAYER_COMMAND, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_CHAT, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_JOIN, playerListener, Event.Priority.Normal, this);
        pm.registerEvent(Event.Type.PLAYER_QUIT, playerListener, Event.Priority.Normal, this);

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
    }

    public void setupPermissions() {
        Plugin test = this.getServer().getPluginManager().getPlugin("Permissions");

        if (test != null)
            usingPermissions = true;
        else
            usingPermissions = false;

    }

    public void saveConfig() {
        Configuration config = new Configuration();

        config.messageFormats.put("local", "{color.WHITE}{prefix}{player}{suffix}{color.CHANNEL}: ");
        config.messageFormats.put("regular", "{color.CHANNEL}[{nick}] {color.WHITE}{prefix}{player}{suffix}{color.CHANNEL}: ");
        config.localDistance = LocalChannel.getDistance();
        config.defaultChannel = defaultChannel.getName();

        for (Channel c : channels) {
            if (!c.isSaved())
                continue;

            ChannelWrapper wrapper = new ChannelWrapper();
            ChannelProperties prop = wrapper.channel;

            prop.identifiers.put("name", c.getName());
            prop.identifiers.put("nick", c.getNick());
            prop.color = c.getColor();
            prop.options.put("local", c instanceof LocalChannel);
            prop.options.put("forced", c.isForced());
            prop.options.put("hidden", c.isHidden());
            prop.options.put("auto", c.isAutomaticallyJoined());
            prop.options.put("permanent", c.isPermanent());
            prop.options.put("quickMessagable", c.isQuickMessagable());
            prop.lists.put("moderators", c.getModerators());
            prop.lists.put("bans", c.getBanList());

            config.channels.add(wrapper);
        }

        File file = new File(getDataFolder(), "data.yml");
        Configuration.saveConfig(file, config);
    }

    public void loadConfig() {
        File file = new File(getDataFolder(), "data.yml");
        Configuration config = Configuration.loadConfig(file);

        LocalChannel.setDistance(config.localDistance);

        regMsgFormatter = new MessageFormatter(config.messageFormats.get("regular"));
        localMsgFormatter = new MessageFormatter(config.messageFormats.get("local"));

        channels = new ArrayList<Channel>();

        for (ChannelWrapper wrapper : config.channels) {
            ChannelProperties prop = wrapper.channel;

            Channel channel;
            if (prop.options.get("local")) {
                channel = new LocalChannel(this);
                channel.setFormatter(localMsgFormatter);
            } else {
                channel = new Channel(this);
                channel.setFormatter(regMsgFormatter);
            }

            channel.setName(prop.identifiers.get("name"));
            channel.setNick(prop.identifiers.get("nick"));
            channel.setColor(prop.color);

            channel.setForced(prop.options.get("forced"));
            channel.setHidden(prop.options.get("hidden"));
            channel.setAutomaticallyJoined(prop.options.get("auto"));
            channel.setPermanent(prop.options.get("permanent"));
            channel.setQuickMessagable(prop.options.get("quickMessagable"));
            channel.setModerators(prop.lists.get("moderators"));
            channel.setBanList(prop.lists.get("bans"));

            channels.add(channel);
        }

        defaultChannel = getChannel(config.defaultChannel);
    }
    
    public void log(String log) {
        logger.log(Level.INFO, "[HEROCHAT] "+ log);
    }

    public List<Command> getCommands() {
        return commands;
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public Channel getChannel(String identifier) {
        for (Channel c : channels) {
            if (c.getName().equalsIgnoreCase(identifier) || c.getNick().equalsIgnoreCase(identifier)) {
                return c;
            }
        }

        return null;
    }

    public Channel getDefaultChannel() {
        return defaultChannel;
    }

    public void setDefaultChannel(Channel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public Channel getActiveChannel(Player player) {
        return activeChannelMap.get(player);
    }

    public void setActiveChannel(Player player, Channel channel) {
        activeChannelMap.put(player, channel);
    }

    public HashMap<Player, Channel> getActiveChannelMap() {
        return activeChannelMap;
    }

    public List<String> getIgnoreList(Player player) {
        List<String> ignoreList = ignoreMap.get(player);

        if (ignoreList == null)
            ignoreList = new ArrayList<String>();

        return ignoreList;
    }

    public void createIgnoreList(Player player) {
        ignoreMap.put(player, new ArrayList<String>());
    }

    public HashMap<Player, List<String>> getIgnoreMap() {
        return ignoreMap;
    }

    public MessageFormatter getRegularMessageFormatter() {
        return regMsgFormatter;
    }

    public MessageFormatter getLocalMessageFormatter() {
        return localMsgFormatter;
    }

    public boolean hasPermission(String name, PluginPermission permission) {
        Player p = getServer().getPlayer(name);

        if (p == null || !usingPermissions)
            return false;

        return hasPermission(p, permission);
    }

    public boolean hasPermission(Player player, PluginPermission permission) {
        return Permissions.Security.permission(player, "herochat." + permission.toString().toLowerCase());
    }

    public boolean isUsingPermissions() {
        return usingPermissions;
    }

}
