package com.herocraftonline.dthielke.herochat.channels;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;

public class ChannelManager {

    private HeroChat plugin;
    private List<Channel> channels;
    private Channel defaultChannel;
    private String defaultMsgFormat;
    private int defaultLocalDistance;
    private HashMap<String, String> activeChannels;
    private HashMap<String, List<String>> ignoreLists;

    public ChannelManager(HeroChat plugin) {
        this.plugin = plugin;
        activeChannels = new HashMap<String, String>();
        ignoreLists = new HashMap<String, List<String>>();
    }

    public String[] getPlayerList() {
        return activeChannels.keySet().toArray(new String[0]);
    }

    public void removeFromAll(String name) {
        for (Channel c : channels) {
            c.removePlayer(name);
        }
    }

    public void joinAutoChannels(String name) {
        Player player = plugin.getServer().getPlayer(name);
        if (player != null) {
            String group = plugin.getPermissions().getGroup(player);
            for (Channel c : channels) {
                if (c.isAutoJoined()) {
                    if (c.getWhitelist().isEmpty() || c.getWhitelist().contains(group)) {
                        c.addPlayer(name);
                    }
                }
            }
        }
    }

    public List<Channel> getJoinedChannels(String name) {
        List<Channel> list = new ArrayList<Channel>();
        for (Channel c : channels) {
            if (c.getPlayers().contains(name)) {
                list.add(c);
            }
        }
        if (list.isEmpty()) {
            defaultChannel.addPlayer(name);
            list.add(defaultChannel);
        }
        return list;
    }

    public Channel getActiveChannel(String name) {
        String active = activeChannels.get(name);
        if (active == null) {
            activeChannels.put(name, defaultChannel.getName());
            return defaultChannel;
        }
        return getChannel(active);
    }

    public void setActiveChannel(String player, String channel) {
        activeChannels.put(player, channel);
    }

    public boolean isIgnoring(String ignorer, String ignoree) {
        List<String> ignoreList = ignoreLists.get(ignorer);
        if (ignoreList != null) {
            return ignoreList.contains(ignoree);
        }
        return false;
    }

    public void addIgnore(String ignorer, String ignoree) {
        List<String> ignoreList = ignoreLists.get(ignorer);
        if (ignoreList == null) {
            ignoreList = new ArrayList<String>();
        }
        ignoreList.add(ignoree);
        ignoreLists.put(ignorer, ignoreList);
    }

    public void removeIgnore(String ignorer, String ignoree) {
        List<String> ignoreList = ignoreLists.get(ignorer);
        if (ignoreList != null) {
            ignoreList.remove(ignoree);
        }
    }

    public List<String> getIgnoreList(String name) {
        List<String> ignoreList = ignoreLists.get(name);
        if (ignoreList == null) {
            ignoreList = new ArrayList<String>();
        }
        return ignoreList;
    }

    public void setIgnoreList(String name, List<String> ignoreList) {
        ignoreLists.put(name, ignoreList);
    }

    public Channel getChannel(String name) {
        for (Channel c : channels) {
            if (c.getName().equalsIgnoreCase(name) || c.getNick().equalsIgnoreCase(name)) {
                return c;
            }
        }
        return null;
    }

    public void addChannel(Channel c) {
        channels.add(c);
    }

    public void removeChannel(Channel c) {
        channels.remove(c);
    }

    public List<Channel> getChannels() {
        return channels;
    }

    public void setChannels(List<Channel> channels) {
        this.channels = channels;
    }

    public void setDefaultChannel(Channel defaultChannel) {
        this.defaultChannel = defaultChannel;
    }

    public Channel getDefaultChannel() {
        return defaultChannel;
    }

    public void setDefaultMsgFormat(String defaultMsgFormat) {
        this.defaultMsgFormat = defaultMsgFormat;
    }

    public String getDefaultMsgFormat() {
        return defaultMsgFormat;
    }

    public void setDefaultLocalDistance(int defaultLocalDistance) {
        this.defaultLocalDistance = defaultLocalDistance;
    }

    public int getDefaultLocalDistance() {
        return defaultLocalDistance;
    }

}
