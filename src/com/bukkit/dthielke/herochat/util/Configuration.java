package com.bukkit.dthielke.herochat.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.yaml.snakeyaml.DumperOptions;
import org.yaml.snakeyaml.DumperOptions.FlowStyle;
import org.yaml.snakeyaml.TypeDescription;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;
import org.yaml.snakeyaml.nodes.Tag;
import org.yaml.snakeyaml.representer.Representer;

import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;

public class Configuration {

    public HashMap<String, String> messageFormats;
    public int localDistance;
    public String defaultChannel;
    public List<ChannelWrapper> channels;

    public static class ChannelWrapper {
        
        public static class ChannelProperties {

            public HashMap<String, String> identifiers;
            public HashMap<String, Boolean> options;
            public HashMap<String, List<String>> lists;
            
            public ChatColor color;
    
            public ChannelProperties() {
                identifiers = new HashMap<String, String>();
                options = new HashMap<String, Boolean>();
                lists = new HashMap<String, List<String>>();
            }
        }
        
        public ChannelProperties channel;
        
        public ChannelWrapper() {
            channel = new ChannelProperties();
        }

    }
    
    public Configuration() {
        messageFormats = new HashMap<String, String>();
        localDistance = 0;
        channels = new ArrayList<ChannelWrapper>();
        
    }
    
    public static Configuration loadConfig(File file) {
        Constructor constructor = new Constructor();
        constructor.addTypeDescription(new TypeDescription(Configuration.class, new Tag("configuration")));
        
        Yaml yaml = new Yaml(constructor);
        
        try {
            return (Configuration)yaml.load(new FileReader(file));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return null;
        }
    }
    
    public static void saveConfig(File file, Configuration config) {
        Representer representer = new Representer();
        representer.addClassTag(Configuration.class, new Tag("configuration"));
        
        DumperOptions options = new DumperOptions();
        options.setDefaultFlowStyle(FlowStyle.BLOCK);
        options.setIndent(4);
        
        Yaml yaml = new Yaml(representer, options);
        
        try {
            yaml.dump(config, new FileWriter(file));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

}
