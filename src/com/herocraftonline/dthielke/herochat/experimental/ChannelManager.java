package com.herocraftonline.dthielke.herochat.experimental;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ChannelManager {

    private List<Channel> channels;
    private Channel defaultChannel;
    private HashMap<String, String> activeChannels;
    private HashMap<String, List<String>> ignoreLists;

    public ChannelManager() {
        activeChannels = new HashMap<String, String>();
        ignoreLists = new HashMap<String, List<String>>();
    }
    
    public String getActiveChannel(String name) {
        String active = activeChannels.get(name);
        if (active == null)
            active = defaultChannel.getName();
        return active;
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
            if (c.getName().equals(name) || c.getNick().equals(name)) {
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

}
