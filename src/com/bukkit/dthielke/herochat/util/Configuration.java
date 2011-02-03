package com.bukkit.dthielke.herochat.util;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;

public class Configuration {

    public static class ChannelWrapper {

        public static class ChannelProperties {

            public HashMap<String, String> identifiers;
            public HashMap<String, Boolean> options;
            public HashMap<String, List<String>> lists;
            public HashMap<String, List<String>> permissions;

            public String messageFormat;

            public ChatColor color;

            public ChannelProperties() {
                identifiers = new HashMap<String, String>();
                options = new HashMap<String, Boolean>();
                lists = new HashMap<String, List<String>>();
                permissions = new HashMap<String, List<String>>();
            }
        }

        public ChannelProperties channel;

        public ChannelWrapper() {
            channel = new ChannelProperties();
        }

    }

    public int localDistance;
    public String defaultChannel;
    public String defaultMessageFormat;

    public List<ChannelWrapper> channels;

    public HashMap<String, List<String>> autojoin;

    public Configuration() {
        localDistance = 0;
        channels = new ArrayList<ChannelWrapper>();
        autojoin = new HashMap<String, List<String>>();
    }

}
