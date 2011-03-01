package com.herocraftonline.dthielke.herochat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat.ChatColor;
import com.herocraftonline.dthielke.herochat.HeroChat.PluginPermission;
import com.herocraftonline.dthielke.herochat.util.MessageFormatter;

/**
 * Channel creates the possibility for sending particular Minecraft chat
 * messages only to members of a particular channel. This class holds everything
 * necessary to moderate channels, assign and remove channel joining and
 * speaking privileges, and send formatted messages to all its members.
 */
public class Channel {

    public enum BanResult {
        NO_PERMISSION,
        PLAYER_IS_MODERATOR,
        PLAYER_IS_ADMIN,
        PLAYER_NOT_FOUND,
        PLAYER_ALREADY_BANNED,
        SUCCESS
    }

    public enum KickResult {
        NO_PERMISSION,
        PLAYER_IS_MODERATOR,
        PLAYER_IS_ADMIN,
        PLAYER_NOT_FOUND,
        SUCCESS
    }

    public static final String LOG_FORMAT = "[{nick}] {player}: ";
    public static final String JOIN_FORMAT = "{color.CHANNEL}[{nick}] ";
    protected MessageFormatter logFormatter;
    protected MessageFormatter joinFormatter;

    protected HeroChat plugin;
    protected MessageFormatter formatter;

    protected String name;
    protected String nick;

    protected ChatColor color;

    protected boolean forced;
    protected boolean hidden;
    protected boolean automaticallyJoined;
    protected boolean saved;
    protected boolean permanent;
    protected boolean quickMessagable;
    protected boolean joinMessages;

    protected List<Player> players;
    protected List<String> moderators;
    protected List<String> banList;

    protected List<String> voiceList;
    protected List<String> whiteList;

    /**
     * Sole constructor specifying the parent plugin.
     * 
     * @param plugin
     *            the HeroChatPluginPlugin that will handle this channel
     */
    public Channel(HeroChat plugin) {
        this.plugin = plugin;
        
        logFormatter = new MessageFormatter(plugin, LOG_FORMAT);
        joinFormatter = new MessageFormatter(plugin, JOIN_FORMAT);
        formatter = new MessageFormatter(plugin);

        name = "default";
        nick = "def";

        color = ChatColor.WHITE;

        forced = false;
        hidden = false;
        automaticallyJoined = false;
        saved = false;
        permanent = false;
        quickMessagable = false;
        joinMessages = false;

        players = new ArrayList<Player>();
        moderators = new ArrayList<String>();
        banList = new ArrayList<String>();

        voiceList = new ArrayList<String>();
        whiteList = new ArrayList<String>();
    }

    /**
     * Adds a moderator to the channel.
     * 
     * @param player
     *            the player to add as a moderator
     */
    public void addModerator(Player player) {
        addModerator(player.getName());
    }

    /**
     * Adds a moderator to the channel.
     * 
     * @param name
     *            the name of the player to add as a moderator
     */
    public void addModerator(String name) {
        if (!isModerator(name))
            moderators.add(name);
    }

    /**
     * Adds a player to the channel.
     * 
     * @param player
     *            the player to add
     * @return <code>true</code> if the player was added (checks for duplicates
     *         and for ban list entries); <code>false</code> otherwise.
     */
    public boolean addPlayer(Player player) {
        if (players.contains(player) || banList.contains(player.getName()))
            return false;

        players.add(player);

        if (joinMessages) {
            String msg = player.getDisplayName() + " has joined the channel.";
            List<String> msgLines = joinFormatter.formatMessageWrapped(this, player.getWorld().getName(), player.getName(), player.getDisplayName(), msg, "");

            for (Player p : players) {
                if (p.equals(player))
                    continue;
                for (String line : msgLines) {
                    p.sendMessage(line);
                }
            }
        }
        
        plugin.joinChannel(player, this);
        
        return true;
    }

    /**
     * Bans a player from the channel.
     * 
     * @param sender
     *            the player issuing the ban
     * @param name
     *            the name of the player to ban
     * @return the outcome of the ban attempt
     */
    public BanResult banPlayer(Player sender, String name) {
        if (isBanned(name))
            return BanResult.PLAYER_ALREADY_BANNED;

        KickResult result = kickPlayer(sender, name);

        if (result == KickResult.SUCCESS || result == KickResult.PLAYER_NOT_FOUND)
            banList.add(name);

        return BanResult.valueOf(result.toString());
    }

    /**
     * Gets the channel's ban list.
     * 
     * @return a list of player names currently banned from the channel
     */
    public List<String> getBanList() {
        return banList;
    }

    /**
     * Gets the channel's color.
     * 
     * @return the color of the channel
     */
    public ChatColor getColor() {
        return color;
    }

    /**
     * Gets the name of the channel formatted in the channel's color.
     * 
     * @return the name of the channel in the channel's color
     */
    public String getColoredName() {
        return getColorString() + name;
    }

    /**
     * Gets the channel's color formatted as a usuable string for display in
     * Minecraft.
     * 
     * @return the string representing the color of the channel
     */
    public String getColorString() {
        return color.format();
    }

    /**
     * Gets the {@link MessageFormatter} used to format all of the channel's
     * outgoing messages.
     * 
     * @return the channel's formatter
     */
    public MessageFormatter getFormatter() {
        return formatter;
    }

    /**
     * Gets the channel's list of moderators.
     * 
     * @return a list of player names currently given moderation privileges of
     *         the channel
     */
    public List<String> getModerators() {
        return moderators;
    }

    /**
     * Gets the name of the channel.
     * 
     * @return the channel's name
     */
    public String getName() {
        return name;
    }

    /**
     * Gets the nickname of the channel.
     * 
     * @return the channel's nickname
     */
    public String getNick() {
        return nick;
    }

    /**
     * Gets the channel's list of current users.
     * 
     * @return a list of the players currently in the channel
     */
    public List<Player> getPlayers() {
        return players;
    }

    /**
     * Gets the channel's list of Permissions groups with speaking privileges.
     * An empty listen indicates open speaking privileges for everyone.
     * 
     * @return a list of Permissions group names
     */
    public List<String> getVoiceList() {
        return voiceList;
    }

    /**
     * Gets the channel's list of Permissions groups with joining privileges. An
     * empty listen indicates open joining privileges for everyone.
     * 
     * @return a list of Permissions group names
     */
    public List<String> getWhiteList() {
        return whiteList;
    }

    /**
     * Checks for a player in the channel's player list.
     * 
     * @param player
     *            the player to check for
     * @return <code>true</code> if the player is found in the channel;
     *         <code>false</code> otherwise.
     */
    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    /**
     * Checks for a player in the channel's player list.
     * 
     * @param name
     *            the name of the player to check for
     * @return <code>true</code> if the player is found in the channel;
     *         <code>false</code> otherwise.
     */
    public boolean hasPlayer(String name) {
        for (Player p : players)
            if (p.getName().equalsIgnoreCase(name))
                return true;
        return false;
    }

    /**
     * Checks to see if the channel should be automatically joined by players
     * upon login.
     * 
     * @return <code>true</code> if the channel should be automatically joined;
     *         <code>false</code> otherwise.
     */
    public boolean isAutomaticallyJoined() {
        return automaticallyJoined;
    }

    /**
     * Checks for a player in the channel's ban list.
     * 
     * @param player
     *            the player to check for
     * @return <code>true</code> if the player is banned from the channel;
     *         <code>false</code> otherwise.
     */
    public boolean isBanned(Player player) {
        return isBanned(player.getName());
    }

    /**
     * Checks for a player in the channel's ban list.
     * 
     * @param name
     *            the name of the player to check for
     * @return <code>true</code> if the player is banned from the channel;
     *         <code>false</code> otherwise.
     */
    public boolean isBanned(String name) {
        for (String player : banList)
            if (player.equalsIgnoreCase(name))
                return true;
        return false;
    }

    /**
     * Checks to see whether players should be forced to remain in the channel.
     * 
     * @return <code>true</code> if the players should be forced to remain in
     *         the channel; <code>false</code> otherwise.
     */
    public boolean isForced() {
        return forced;
    }

    /**
     * Checks to see whether the channel should be hidden from the public.
     * 
     * @return <code>true</code> if the channel should be hidden from the
     *         public; <code>false</code> otherwise.
     */
    public boolean isHidden() {
        return hidden;
    }

    /**
     * Checks for a player in the channel's moderator list,
     * 
     * @param player
     *            the player to check for
     * @return <code>true</code> if the player is a moderator of the channel;
     *         <code>false</code> otherwise.
     */
    public boolean isModerator(Player player) {
        return isModerator(player.getName());
    }

    /**
     * Checks for a player in the channel's moderator list,
     * 
     * @param name
     *            the name of the player to check for
     * @return <code>true</code> if the player is a moderator of the channel;
     *         <code>false</code> otherwise.
     */
    public boolean isModerator(String name) {
        for (String mod : moderators)
            if (mod.equalsIgnoreCase(name))
                return true;
        return false;
    }

    /**
     * Checks to see whether the channel should only be removable via a
     * configuration file.
     * 
     * @return <code>true</code> if the channel is permanent; <code>false</code>
     *         otherwise.
     */
    public boolean isPermanent() {
        return permanent;
    }

    /**
     * Checks to see whether the channel should be accessible via the channel
     * shortcut command ("/<channel> <msg>").
     * 
     * @return <code>true</code> if the channel should be accessible via the
     *         channel shortcut command; <code>false</code> otherwise.
     */
    public boolean isQuickMessagable() {
        return quickMessagable;
    }

    /**
     * Checks to see whether the channel should be saved to a configuration
     * file.
     * 
     * @return <code>true</code> if the channel should be saved;
     *         <code>false</code> otherwise.
     */
    public boolean isSaved() {
        return saved;
    }

    /**
     * Kicks a player from the channel.
     * 
     * @param sender
     *            the player issuing the kick
     * @param name
     *            the name of the player to be kicked
     * @return the outcome of the kick attempt
     */
    public KickResult kickPlayer(Player sender, String name) {
        if (!isModerator(sender) && !plugin.hasPermission(sender, PluginPermission.ADMIN)) {
            return KickResult.NO_PERMISSION;
        }

        if (isModerator(name))
            return KickResult.PLAYER_IS_MODERATOR;

        if (plugin.hasPermission(name, PluginPermission.ADMIN))
            return KickResult.PLAYER_IS_ADMIN;

        for (Player p : players) {
            if (p.getName().equalsIgnoreCase(name)) {
                removePlayer(p);
                return KickResult.SUCCESS;
            }
        }

        return KickResult.PLAYER_NOT_FOUND;
    }

    /**
     * Removes a player from the channel.
     * 
     * @param player
     *            the player to be removed
     * @return <code>true</code> if the player was removed; <code>false</code>
     *         otherwise.
     */
    public boolean removePlayer(Player player) {
        if (!players.contains(player))
            return false;

        players.remove(player);
        if (plugin.getActiveChannel(player).getName().equals(this.name)) {
            List<Channel> channels = plugin.getJoinedChannels(player);
            for (Channel c : channels)
                if (!c.getName().equals(name)) {
                    plugin.setActiveChannel(player, c);
                    player.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Set active channel to " + c.getColoredName());
                    break;
                }
        }

        if (joinMessages) {
            String msg = player.getDisplayName() + " has left the channel.";
            List<String> msgLines = joinFormatter.formatMessageWrapped(this, player.getWorld().getName(), player.getName(), player.getDisplayName(), msg, "");

            for (Player p : players) {
                for (String line : msgLines) {
                    p.sendMessage(line);
                }
            }
        }
        
        plugin.leaveChannel(player, this);

        return true;
    }

    /**
     * Sends a message to the players in the channel. The message is censored
     * and formatted before sending. If a channel member has the sender on
     * ignore, the message is not sent to the ignoring player.
     * 
     * @param sender
     *            the player sending the message
     * @param msg
     *            the message to be sent
     */
    public void sendMessage(Player sender, String msg) {
        if (!voiceList.isEmpty()) {
            String group = plugin.security.getGroup(sender.getWorld().getName(), sender.getName());

            if (!voiceList.contains(group)) {
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You cannot speak in this channel");
                return;
            }
        }

        msg = plugin.censor(msg);

        List<String> msgLines = formatter.formatMessageWrapped(this, sender.getWorld().getName(), sender.getName(), sender.getDisplayName(), msg, plugin.getHealthBar(sender));

        for (Player p : players) {
            if (!plugin.getIgnoreList(p).contains(sender.getName())) {
                for (String line : msgLines) {
                    p.sendMessage(line);
                }
            }
        }

        plugin.log(logFormatter.formatMessage(this, sender.getWorld().getName(), sender.getName(), sender.getDisplayName(), msg, plugin.getHealthBar(sender)));
    }
    
    public void sendMessage(String source, String msg) {
        msg = plugin.censor(msg);
        
        List<String> msgLines = formatter.formatMessageWrapped(this, "", source, source, msg, "");
        for (Player p : players) {
            if (!plugin.getIgnoreList(p).contains(source)) {
                for (String line : msgLines) {
                    p.sendMessage(line);
                }
            }
        }
        plugin.log(logFormatter.formatMessage(this, "", source, source, msg, ""));
    }

    /**
     * Sets whether the channel should be automatically joined by players upon
     * login.
     * 
     * @param automaticallyJoined
     *            <code>true</code> if the channel should be automatically
     *            joined; <code>false</code> otherwise.
     */
    public void setAutomaticallyJoined(boolean automaticallyJoined) {
        this.automaticallyJoined = automaticallyJoined;
    }

    /**
     * Sets the list of player names banned from the channel.
     * 
     * @param banList
     *            the list of banned player names
     */
    public void setBanList(List<String> banList) {
        this.banList = banList;
    }

    /**
     * Sets the color of the channel.
     * 
     * @param color
     *            the channel's new color
     */
    public void setColor(ChatColor color) {
        this.color = color;
    }

    /**
     * Sets whether players should be forced to remain in this channel.
     * 
     * @param forced
     *            <code>true</code> if the players should be forced to remain in
     *            the channel; <code>false</code> otherwise.
     */
    public void setForced(boolean forced) {
        this.forced = forced;
    }

    /**
     * Sets the {@link MessageFormatter} this channel will use to format all
     * outgoing messages.
     * 
     * @param formatter
     *            the formatter to be used
     */
    public void setFormatter(MessageFormatter formatter) {
        this.formatter = formatter;
    }

    /**
     * Sets whether the channel should be hidden from the public.
     * 
     * @param hidden
     *            <code>true</code> if the channel should be hidden from the
     *            public; <code>false</code> otherwise.
     */
    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    /**
     * Sets the list of player names with moderation privileges of the channel.
     * 
     * @param moderators
     *            the list of moderating player names
     */
    public void setModerators(List<String> moderators) {
        this.moderators = moderators;
    }

    /**
     * Sets the name of the channel.
     * 
     * @param name
     *            the name of the channel
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Sets the nickname of the channel.
     * 
     * @param nick
     *            the nickname of the channel
     */
    public void setNick(String nick) {
        this.nick = nick;
    }

    /**
     * Sets whether the channel should be removable only via a configuration
     * file.
     * 
     * @param permanent
     *            <code>true</code> if the channel is permanent;
     *            <code>false</code> otherwise.
     */
    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    /**
     * Sets the list of players currently in the channel.
     * 
     * @param players
     *            the list of players in the channel
     */
    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    /**
     * Sets whether the channel should be accessible via the channel shortcut
     * command ("/<channel> <msg>").
     * 
     * @param quickMessagable
     *            <code>true</code> if the channel should be accessible via the
     *            channel shortcut command; <code>false</code> otherwise.
     */
    public void setQuickMessagable(boolean quickMessagable) {
        this.quickMessagable = quickMessagable;
    }

    /**
     * Sets whether the channel should be saved to a configuration file.
     * 
     * @param saved
     *            <code>true</code> if the channel should be saved;
     *            <code>false</code> otherwise.
     */
    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    /**
     * Sets the list of player names with speaking privileges in the channel.
     * 
     * @param voiceList
     *            the list of names with speaking privileges
     */
    public void setVoiceList(List<String> voiceList) {
        this.voiceList = voiceList;
    }

    /**
     * Sets the list of player names with the privilege to join the channel.
     * 
     * @param whiteList
     *            the list of names with joining privileges
     */
    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

    /**
     * Removes a previous ban on a player.
     * 
     * @param sender
     *            the player removing the ban
     * @param name
     *            the name of the player to be unbanned
     * @return the outcome of the ban removal attempt
     */
    public BanResult unbanPlayer(Player sender, String name) {
        if (!isModerator(sender) && !plugin.hasPermission(sender, PluginPermission.ADMIN)) {
            return BanResult.NO_PERMISSION;
        }

        for (String s : banList) {
            if (s.equalsIgnoreCase(name)) {
                banList.remove(s);
                return BanResult.SUCCESS;
            }
        }

        return BanResult.PLAYER_NOT_FOUND;
    }

    public boolean isJoinMessages() {
        return joinMessages;
    }

    public void setJoinMessages(Boolean joinMessages) {
        if (joinMessages != null)
            this.joinMessages = joinMessages;
        else {
            this.joinMessages = false;
        }
    }

}
