package com.bukkit.dthielke.herochat;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;
import com.bukkit.dthielke.herochat.HeroChatPlugin.PluginPermission;
import com.bukkit.dthielke.herochat.util.MessageFormatter;

public class Channel {

    public enum KickResult {
        NO_PERMISSION,
        PLAYER_IS_MODERATOR,
        PLAYER_IS_ADMIN,
        PLAYER_NOT_FOUND,
        SUCCESS
    }
    
    public enum BanResult {
        NO_PERMISSION,
        PLAYER_IS_MODERATOR,
        PLAYER_IS_ADMIN,
        PLAYER_NOT_FOUND,
        PLAYER_ALREADY_BANNED,
        SUCCESS
    }
    
    public static final String logFormat = "[{nick}] {player}: ";

    protected HeroChatPlugin plugin;
    protected MessageFormatter formatter;
    protected MessageFormatter logFormatter;

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

    public Channel(HeroChatPlugin plugin) {
        this.plugin = plugin;
        formatter = new MessageFormatter();
        logFormatter = new MessageFormatter(logFormat);

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
    }

    public void sendMessage(Player sender, String msg) {
        List<String> msgLines = formatter.formatMessageWrapped(this, sender.getName(), msg, plugin.isUsingPermissions());

        for (Player p : players) {
            if (!plugin.getIgnoreList(p).contains(sender.getName())) {
                for (String line : msgLines) {
                    p.sendMessage(line);
                }
            }
        }
        
        plugin.log(logFormatter.formatMessage(this, sender.getName(), msg, plugin.isUsingPermissions()));
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

    public boolean addPlayer(Player player) {
        if (players.contains(player) || banList.contains(player.getName()))
            return false;

        players.add(player);
        return true;
    }

    public boolean removePlayer(Player player) {
        if (!players.contains(player))
            return false;

        players.remove(player);
        return true;
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

    public BanResult banPlayer(Player sender, String name) {
        if (isBanned(name))
            return BanResult.PLAYER_ALREADY_BANNED;
        
        KickResult result = kickPlayer(sender, name);

        if (result == KickResult.SUCCESS)
            banList.add(name);
        
        return BanResult.valueOf(result.toString());
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

    public String getColorString() {
        return color.format();
    }

    public String getColoredName() {
        return getColorString() + name;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isAutomaticallyJoined() {
        return automaticallyJoined;
    }

    public void setAutomaticallyJoined(boolean automaticallyJoined) {
        this.automaticallyJoined = automaticallyJoined;
    }

    public boolean isSaved() {
        return saved;
    }

    public void setSaved(boolean saved) {
        this.saved = saved;
    }

    public boolean isPermanent() {
        return permanent;
    }

    public void setPermanent(boolean permanent) {
        this.permanent = permanent;
    }

    public boolean isQuickMessagable() {
        return quickMessagable;
    }

    public void setQuickMessagable(boolean quickMessagable) {
        this.quickMessagable = quickMessagable;
    }

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public List<Player> getPlayers() {
        return players;
    }

    public void setPlayers(List<Player> players) {
        this.players = players;
    }
    
    public void addModerator(Player player) {
        addModerator(player.getName());
    }
    
    public void addModerator(String name) {
        if (!isModerator(name))
            moderators.add(name);
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

    public List<String> getModerators() {
        return moderators;
    }

    public void setModerators(List<String> moderators) {
        this.moderators = moderators;
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

    public List<String> getBanList() {
        return banList;
    }

    public void setBanList(List<String> banList) {
        this.banList = banList;
    }

    public MessageFormatter getFormatter() {
        return formatter;
    }

    public void setFormatter(MessageFormatter formatter) {
        this.formatter = formatter;
    }

}