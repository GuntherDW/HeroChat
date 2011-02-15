package com.bukkit.dthielke.herochat.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import com.bukkit.dthielke.herochat.Channel;
import com.bukkit.dthielke.herochat.HeroChatPlugin;
import com.bukkit.dthielke.herochat.LocalChannel;
import com.bukkit.dthielke.herochat.util.Configuration.ChannelWrapper;
import com.bukkit.dthielke.herochat.util.Configuration.ChannelWrapper.ChannelProperties;

public class ConfigurationHandler {
    public static void dumpToFile(Configuration config, File file) {
        Representer representer = new Representer();
        representer.addClassTag(Configuration.class, new Tag("configuration"));

        DumperOptions options = new DumperOptions();
        options.setWidth(300);
        options.setIndent(4);

        Yaml yaml = new Yaml(representer, options);

        try {
            yaml.dump(config, new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static void load(HeroChatPlugin plugin, File file) {
        Configuration config = parseFile(file);

        LocalChannel.setDistance(config.localDistance);

        MessageFormatter.setDefaultMessageFormat(config.defaultMessageFormat);

//        plugin.setAutoJoinMap(config.autojoin);

        List<Channel> channels = new ArrayList<Channel>();

        for (ChannelWrapper wrapper : config.channels) {
            ChannelProperties prop = wrapper.channel;

            Channel channel;
            if (prop.options.get("local"))
                channel = new LocalChannel(plugin);
            else
                channel = new Channel(plugin);

            channel.setFormatter(new MessageFormatter(prop.messageFormat));

            channel.setName(prop.identifiers.get("name"));
            channel.setNick(prop.identifiers.get("nick"));
            channel.setColor(prop.color);

            channel.setForced(prop.options.get("forced"));
            channel.setHidden(prop.options.get("hidden"));
            channel.setAutomaticallyJoined(prop.options.get("auto"));
            channel.setPermanent(prop.options.get("permanent"));
            channel.setQuickMessagable(prop.options.get("quickMessagable"));
            channel.setJoinMessages(prop.options.get("joinMessages"));
            channel.setModerators(prop.lists.get("moderators"));
            channel.setBanList(prop.lists.get("bans"));

            channel.setWhiteList(prop.permissions.get("join"));
            channel.setVoiceList(prop.permissions.get("speak"));

            channel.setSaved(true);

            channels.add(channel);
        }

        plugin.setChannels(channels);
        plugin.setDefaultChannel(plugin.getChannel(config.defaultChannel));
    }

    public static Configuration parseFile(File file) {
        Constructor constructor = new Constructor();
        constructor.addTypeDescription(new TypeDescription(Configuration.class, new Tag("configuration")));

        Yaml yaml = new Yaml(constructor);

        try {
            return (Configuration) yaml.load(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }

    public static void save(HeroChatPlugin plugin, File file) {
        plugin.log("Saving...");

        Configuration config = new Configuration();

        config.localDistance = LocalChannel.getDistance();
        config.defaultChannel = plugin.getDefaultChannel().getName();
        config.defaultMessageFormat = MessageFormatter.getDefaultMessageFormat();
//        config.autojoin = plugin.getAutoJoinMap();

        for (Channel c : plugin.getChannels()) {
            if (!c.isSaved())
                continue;

            ChannelWrapper wrapper = new ChannelWrapper();
            ChannelProperties prop = wrapper.channel;

            prop.identifiers.put("name", c.getName());
            prop.identifiers.put("nick", c.getNick());
            prop.color = c.getColor();
            prop.messageFormat = c.getFormatter().getFormat();
            prop.options.put("local", c instanceof LocalChannel);
            prop.options.put("forced", c.isForced());
            prop.options.put("hidden", c.isHidden());
            prop.options.put("auto", c.isAutomaticallyJoined());
            prop.options.put("permanent", c.isPermanent());
            prop.options.put("quickMessagable", c.isQuickMessagable());
            prop.options.put("joinMessages", c.isJoinMessages());
            prop.lists.put("moderators", c.getModerators());
            prop.lists.put("bans", c.getBanList());
            prop.permissions.put("join", c.getWhiteList());
            prop.permissions.put("speak", c.getVoiceList());

            config.channels.add(wrapper);
        }

        dumpToFile(config, file);

        plugin.log("Save completed.");
    }

}
