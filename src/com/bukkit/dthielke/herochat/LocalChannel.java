package com.bukkit.dthielke.herochat;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;
import com.nijikokun.bukkit.Permissions.Permissions;

public class LocalChannel extends Channel {

    public static final int DEFAULT_DISTANCE = 100;

    protected static int distance = DEFAULT_DISTANCE;

    public LocalChannel(HeroChatPlugin plugin) {
        super(plugin);
    }

    @Override
    public void sendMessage(Player sender, String msg) {
        if (plugin.isUsingPermissions() && !voiceList.isEmpty()) {
            String group = Permissions.Security.getGroup(sender.getName());
            
            if (!voiceList.contains(group)) {
                sender.sendMessage("HeroChat: You cannot speak in this channel");
                return;
            }
        }
        
        List<String> msgLines = formatter.formatMessageWrapped(this, sender.getName(), sender.getDisplayName(), msg, plugin.getHealthBar(sender), plugin.isUsingPermissions());

        boolean heard = false;

        Vector senderLoc = sender.getLocation().toVector();
        String senderName = sender.getName();

        for (Player p : players) {
            if (!plugin.getIgnoreList(p).contains(sender.getName())) {
                if (p.getLocation().toVector().distance(senderLoc) <= distance) {
                    for (String line : msgLines)
                        p.sendMessage(line);

                    if (!p.getName().equalsIgnoreCase(senderName))
                        heard = true;
                }
            }
        }

        if (!heard)
            sender.sendMessage(ChatColor.GRAY.format() + "No one hears you.");

        plugin.log(logFormatter.formatMessage(this, senderName, sender.getDisplayName(), msg, plugin.getHealthBar(sender), false));
    }

    public static void setDistance(int distance) {
        LocalChannel.distance = distance;
    }

    public static int getDistance() {
        return distance;
    }
}
