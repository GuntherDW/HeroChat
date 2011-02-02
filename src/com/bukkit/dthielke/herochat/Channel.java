package com.bukkit.dthielke.herochat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;
import com.bukkit.dthielke.herochat.HeroChatPlugin.PluginPermission;
import com.bukkit.dthielke.herochat.util.MessageFormatter;
import com.nijikokun.bukkit.Permissions.Permissions;

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
    public static final MessageFormatter LOG_FORMATTER = new MessageFormatter(LOG_FORMAT);

    protected HeroChatPlugin plugin;
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

    protected List<Player> players;
    protected List<String> moderators;
    protected List<String> banList;
    
    protected List<String> voiceList;
    protected List<String> whiteList;

    public Channel(HeroChatPlugin plugin) {
        this.plugin = plugin;
        formatter = new MessageFormatter();

        name = "default";
        nick = "def";

        color = ChatColor.WHITE;

        forced = false;
        hidden = false;
        automaticallyJoined = false;
        saved = false;
        permanent = false;
        quickMessagable = false;

        players = new ArrayList<Player>();
        moderators = new ArrayList<String>();
        banList = new ArrayList<String>();
        
        voiceList = new ArrayList<String>();
        whiteList = new ArrayList<String>();
    }

    public void addModerator(Player player) {
        addModerator(player.getName());
    }

    public void addModerator(String name) {
        if (!isModerator(name))
            moderators.add(name);
    }

    public boolean addPlayer(Player player) {
        if (players.contains(player) || banList.contains(player.getName()))
            return false;

        players.add(player);
        return true;
    }

    public BanResult banPlayer(Player sender, String name) {
        if (isBanned(name))
            return BanResult.PLAYER_ALREADY_BANNED;

        KickResult result = kickPlayer(sender, name);

        if (result == KickResult.SUCCESS)
            banList.add(name);

        return BanResult.valueOf(result.toString());
    }

    public List<String> getBanList() {
        return banList;
    }

    public ChatColor getColor() {
        return color;
    }

    public String getColoredName() {
        return getColorString() + name;
    }

    public String getColorString() {
        return color.format();
    }

    public MessageFormatter getFormatter() {
        return formatter;
    }

    public List<String> getModerators() {
        return moderators;
    }

    public String getName() {
        return name;
    }

    public String getNick() {
        return nick;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public List<String> getVoiceList() {
        return voiceList;
    }

    public List<String> getWhiteList() {
        return whiteList;
    }

    public boolean hasPlayer(Player player) {
        return players.contains(player);
    }

    public boolean hasPlayer(String name) {
        for (Player p : players)
            if (p.getName().equalsIgnoreCase(name))
                return true;
        return false;
    }

    public boolean isAutomaticallyJoined() {
        return automaticallyJoined;
    }

    public boolean isBanned(Player player) {
        return isBanned(player.getName());
    }

    public boolean isBanned(String name) {
        for (String player : banList)
            if (player.equalsIgnoreCase(name))
                return true;
        return false;
    }

    public boolean isForced() {
        return forced;
    }

    public boolean isHidden() {
        return hidden;
    }

    public boolean isModerator(Player player) {
        return isModerator(player.getName());
    }

    public boolean isModerator(String name) {
        for (String mod : moderators)
            if (mod.equalsIgnoreCase(name))
                return true;
        return false;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public boolean isQuickMessagable() {
        return quickMessagable;
    }

    public boolean isSaved() {
        return saved;
    }

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
                players.remove(p);
                return KickResult.SUCCESS;
            }
        }

        return KickResult.PLAYER_NOT_FOUND;
    }

    public boolean removePlayer(Player player) {
        if (!players.contains(player))
            return false;

        players.remove(player);
        return true;
    }

    public void sendMessage(Player sender, String msg) {
        if (plugin.isUsingPermissions() && !voiceList.isEmpty()) {
            String group = Permissions.Security.getGroup(sender.getName());
            
            if (!voiceList.contains(group)) {
                sender.sendMessage("HeroChat: You cannot speak in this channel");
                return;
            }
        }
        
        msg = plugin.censor(msg);
        
        List<String> msgLines = formatter.formatMessageWrapped(this, sender.getName(), sender.getDisplayName(), msg, plugin.getHealthBar(sender), plugin.isUsingPermissions());

        for (Player p : players) {
            if (!plugin.getIgnoreList(p).contains(sender.getName())) {
                for (String line : msgLines) {
                    p.sendMessage(line);
                }
            }
        }

        plugin.log(LOG_FORMATTER.formatMessage(this, sender.getName(), sender.getDisplayName(), msg, plugin.getHealthBar(sender), false));
    }

    public void setAutomaticallyJoined(boolean automaticallyJoined) {
        this.automaticallyJoined = automaticallyJoined;
    }

    public void setBanList(List<String> banList) {
        this.banList = banList;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public void setFormatter(MessageFormatter formatter) {
        this.formatter = formatter;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public void setModerators(List<String> moderators) {
        this.moderators = moderators;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }

    public void setQuickMessagable(boolean quickMessagable) {
        this.quickMessagable = quickMessagable;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public void setVoiceList(List<String> voiceList) {
        this.voiceList = voiceList;
    }

    public void setWhiteList(List<String> whiteList) {
        this.whiteList = whiteList;
    }

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

}
