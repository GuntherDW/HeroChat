package com.herocraftonline.dthielke.herochat.channels;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.HeroChat.ChatColor;
import com.herocraftonline.dthielke.herochat.util.Messaging;

public class Channel {

    protected static final String logFormat = "[{nick}] {player}: ";
    protected static final String joinFormat = "{color.CHANNEL}[{nick}] ";

    protected HeroChat plugin;

    protected String name;
    protected String nick;
    protected String msgFormat;
    protected ChatColor color;

    protected boolean verbose;
    protected boolean hidden;
    protected boolean forced;
    protected boolean autoJoined;
    protected boolean quickMessagable;

    protected List<String> players;
    protected List<String> moderators;
    protected List<String> blacklist;
    protected List<String> whitelist;
    protected List<String> voicelist;

    public Channel(HeroChat plugin) {
        this.plugin = plugin;
        this.msgFormat = "{default}";
        this.color = ChatColor.WHITE;
        
        players = new ArrayList<String>();
        moderators = new ArrayList<String>();
        blacklist = new ArrayList<String>();
        whitelist = new ArrayList<String>();
        voicelist = new ArrayList<String>();
    }

    public void sendMessage(String name, String msg) {
        List<String> formattedMsg = Messaging.formatWrapped(plugin, this, msgFormat, name, msg);
        ChannelManager cm = plugin.getChannelManager();
        for (String other : players) {
            if (!cm.isIgnoring(other, name)) {
                Player receiver = plugin.getServer().getPlayer(other);
                if (receiver != null) {
                    for (String line : formattedMsg) {
                        receiver.sendMessage(line);
                    }
                }
            }
        }
        String logMsg = Messaging.format(plugin, this, logFormat, name, msg);
        plugin.log(logMsg);
    }

    public void addPlayer(String name) {
        if (!players.contains(name) && !blacklist.contains(name)) {
            players.add(name);
            if (verbose) {
                Player p = plugin.getServer().getPlayer(name);
                if (p != null) {
                    String msg = p.getDisplayName() + " has joined the channel";
                    List<String> msgLines = Messaging.formatWrapped(plugin, this, joinFormat, "", msg);

                    for (String s : players) {
                        Player other = plugin.getServer().getPlayer(s);
                        if (!p.equals(other)) {
                            for (String line : msgLines) {
                                other.sendMessage(line);
                            }
                        }
                    }
                }
            }
        }
    }

    public void removePlayer(String name) {
        if (players.contains(name)) {
            players.remove(name);
            if (verbose) {
                Player p = plugin.getServer().getPlayer(name);
                if (p != null) {
                    String msg = p.getDisplayName() + " has left the channel";
                    List<String> msgLines = Messaging.formatWrapped(plugin, this, joinFormat, "", msg);

                    for (String s : players) {
                        Player other = plugin.getServer().getPlayer(s);
                        if (!p.equals(other)) {
                            for (String line : msgLines) {
                                other.sendMessage(line);
                            }
                        }
                    }
                }
            }
        }
    }

    public String getCName() {
        return color.str + name;
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

    public ChatColor getColor() {
        return color;
    }

    public void setColor(ChatColor color) {
        this.color = color;
    }

    public List<String> getBlacklist() {
        return blacklist;
    }

    public void setBlacklist(List<String> blacklist) {
        this.blacklist = blacklist;
    }

    public List<String> getWhitelist() {
        return whitelist;
    }

    public void setWhitelist(List<String> whitelist) {
        this.whitelist = whitelist;
    }

    public List<String> getVoicelist() {
        return voicelist;
    }

    public void setVoicelist(List<String> voicelist) {
        this.voicelist = voicelist;
    }

    public List<String> getPlayers() {
        return players;
    }

    public String getMsgFormat() {
        return msgFormat;
    }

    public void setMsgFormat(String msgFormat) {
        this.msgFormat = msgFormat;
    }

    public List<String> getModerators() {
        return moderators;
    }

    public void setModerators(List<String> moderators) {
        this.moderators = moderators;
    }

    public boolean isVerbose() {
        return verbose;
    }

    public void setVerbose(boolean verbose) {
        this.verbose = verbose;
    }

    public boolean isHidden() {
        return hidden;
    }

    public void setHidden(boolean hidden) {
        this.hidden = hidden;
    }

    public boolean isForced() {
        return forced;
    }

    public void setForced(boolean forced) {
        this.forced = forced;
    }

    public boolean isAutoJoined() {
        return autoJoined;
    }

    public void setAutoJoined(boolean autoJoined) {
        this.autoJoined = autoJoined;
    }

    public boolean isQuickMessagable() {
        return quickMessagable;
    }

    public void setQuickMessagable(boolean quickMessagable) {
        this.quickMessagable = quickMessagable;
    }

}
