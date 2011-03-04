/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.herocraftonline.dthielke.herochat.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.bukkit.util.config.Configuration;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.HeroChat.ChatColor;
import com.herocraftonline.dthielke.herochat.channels.Channel;
import com.herocraftonline.dthielke.herochat.channels.ChannelManager;
import com.herocraftonline.dthielke.herochat.channels.LocalChannel;

public class ConfigManager {
    protected HeroChat plugin;
    protected File primaryConfigFile;
    protected File usersConfigFile;
    protected Configuration usersConfig;

    public ConfigManager(HeroChat plugin) {
        this.plugin = plugin;
        this.primaryConfigFile = new File(plugin.getDataFolder(), "config.yml");
        this.usersConfigFile = new File(plugin.getDataFolder(), "users.yml");
        this.usersConfig = new Configuration(usersConfigFile);
    }

    public void reload() {
        load();
    }

    public void load() {
        Configuration config = new Configuration(primaryConfigFile);
        config.load();
        loadChannels(config);
        loadGlobals(config);
        usersConfig.load();
    }

    private void loadGlobals(Configuration config) {
        String globals = "globals.";
        ChannelManager cm = plugin.getChannelManager();
        String pluginTag = config.getString(globals + "plugin-tag", "[HeroChat] ").replace("&", "§");
        String defaultChannel = config.getString(globals + "default-channel", cm.getChannels().get(0).getName());
        String defaultMsgFormat = config.getString(globals + "default-message-format", "{name}: ");
        int defaultLocalDistance = config.getInt(globals + "default-local-distance", 100);

        plugin.setTag(pluginTag);
        cm.setDefaultChannel(cm.getChannel(defaultChannel));
        cm.setDefaultMsgFormat(defaultMsgFormat);
        cm.setDefaultLocalDistance(defaultLocalDistance);

        for (Channel c : cm.getChannels()) {
            if (c instanceof LocalChannel) {
                LocalChannel l = (LocalChannel) c;
                l.setDistance(cm.getDefaultLocalDistance());
            }
        }
    }

    private void loadChannels(Configuration config) {
        List<Channel> list = new ArrayList<Channel>();
        for (String s : config.getKeys("channels")) {
            String root = "channels." + s + ".";
            Channel c;
            if (config.getBoolean(root + "options.local", false)) {
                c = new LocalChannel(plugin);
            } else {
                c = new Channel(plugin);
            }

            c.setName(s);
            c.setNick(config.getString(root + "nickname", "DEFAULT-NICK"));
            c.setColor(ChatColor.valueOf(config.getString(root + "color", "WHITE")));
            c.setMsgFormat(config.getString(root + "message-format", "{default}"));
            c.setWorlds(config.getStringList(root + "worlds", null));

            String options = root + "options.";
            c.setVerbose(config.getBoolean(options + "join-messages", true));
            c.setQuickMessagable(config.getBoolean(options + "shortcut-allowed", false));
            c.setHidden(config.getBoolean(options + "hidden", false));
            c.setAutoJoined(config.getBoolean(options + "auto-join", false));
            c.setForced(config.getBoolean(options + "forced", false));

            String lists = root + "lists.";
            c.setBlacklist(config.getStringList(lists + "bans", null));
            c.setModerators(config.getStringList(lists + "moderators", null));

            String permissions = root + "permissions.";
            c.setWhitelist(config.getStringList(permissions + "join", null));
            c.setVoicelist(config.getStringList(permissions + "speak", null));

            list.add(c);
        }
        plugin.getChannelManager().setChannels(list);
    }

    @SuppressWarnings("unused")
    private void loadPlayers() {
        Configuration config = new Configuration(usersConfigFile);
        config.load();
        try {
            for (String name : config.getKeys("users")) {
                loadPlayer(name, config);
            }
        } catch (Exception e) {}
    }

    public void loadPlayer(String name) {
        loadPlayer(name, usersConfig);
    }

    private void loadPlayer(String name, Configuration config) {
        ChannelManager cm = plugin.getChannelManager();
        try {
            String activeChannel = config.getString("users." + name + ".active-channel", cm.getDefaultChannel().getName());
            Channel a = cm.getChannel(activeChannel);
            if (a != null) {
                cm.setActiveChannel(name, activeChannel);
            } else {
                cm.setActiveChannel(name, cm.getDefaultChannel().getName());
            }

            List<String> joinedChannels = config.getStringList("users." + name + ".joined-channels", null);
            if (joinedChannels.isEmpty()) {
                cm.joinAutoChannels(name);
            } else {
                for (String s : joinedChannels) {
                    Channel c = cm.getChannel(s);
                    if (c != null) {
                        if (!c.getBlacklist().contains(name)) {
                            c.addPlayer(name);
                        }
                    }
                }
            }
        } catch (Exception e) {
            cm.setActiveChannel(name, cm.getDefaultChannel().getName());
            cm.joinAutoChannels(name);
        }
    }

    public void save() {
        Configuration config = new Configuration(primaryConfigFile);
        saveGlobals(config);
        saveChannels(config);
        config.save();

        usersConfig.save();
    }

    private void saveGlobals(Configuration config) {
        ChannelManager cm = plugin.getChannelManager();
        String globals = "globals.";
        config.setProperty(globals + "plugin-tag", plugin.getTag());
        config.setProperty(globals + "default-channel", cm.getDefaultChannel().getName());
        config.setProperty(globals + "default-message-format", cm.getDefaultMsgFormat());
        config.setProperty(globals + "default-local-distance", cm.getDefaultLocalDistance());
    }

    private void saveChannels(Configuration config) {
        Channel[] channels = plugin.getChannelManager().getChannels().toArray(new Channel[0]);
        for (Channel c : channels) {
            String root = "channels." + c.getName() + ".";
            config.setProperty(root + "nickname", c.getNick());
            config.setProperty(root + "color", c.getColor().toString());
            config.setProperty(root + "message-format", c.getMsgFormat());
            config.setProperty(root + "worlds", c.getWorlds());

            String options = root + "options.";
            config.setProperty(options + "join-messages", c.isVerbose());
            config.setProperty(options + "shortcut-allowed", c.isQuickMessagable());
            config.setProperty(options + "hidden", c.isHidden());
            config.setProperty(options + "auto-join", c.isAutoJoined());
            config.setProperty(options + "local", c instanceof LocalChannel);
            config.setProperty(options + "forced", c.isForced());

            String lists = root + "lists.";
            config.setProperty(lists + "bans", c.getBlacklist());
            config.setProperty(lists + "moderators", c.getModerators());

            String permissions = root + "permissions.";
            config.setProperty(permissions + "join", c.getWhitelist());
            config.setProperty(permissions + "speak", c.getVoicelist());
        }
    }

    @SuppressWarnings("unused")
    private void savePlayers() {
        for (String name : plugin.getChannelManager().getPlayerList()) {
            savePlayer(name, usersConfig);
        }
        usersConfig.save();
    }

    public void savePlayer(String name) {
        savePlayer(name, usersConfig);
    }

    private void savePlayer(String name, Configuration config) {
        try {
            ChannelManager cm = plugin.getChannelManager();
            Channel active = cm.getActiveChannel(name);
            List<Channel> joined = cm.getJoinedChannels(name);
            List<String> names = new ArrayList<String>();
            for (Channel c : joined) {
                names.add(c.getName());
            }
            config.setProperty("users." + name + ".active-channel", active.getName());
            config.setProperty("users." + name + ".joined-channels", names);
        } catch (Exception e) {
            e.printStackTrace();
            plugin.log("Error saving player data. Delete your users.yml");
        }
    }
}
