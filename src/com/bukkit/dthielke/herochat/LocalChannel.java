package com.bukkit.dthielke.herochat;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.util.Vector;

import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;

public class LocalChannel extends Channel {

    public static final int DEFAULT_DISTANCE = 100;

    protected static int distance = DEFAULT_DISTANCE;

    public LocalChannel(HeroChatPlugin plugin) {
        super(plugin);
    }

    @Override
    public void sendMessage(Player sender, String msg) {
        List<String> msgLines = formatter.formatMessageWrapped(this, sender.getName(), msg, plugin.isUsingPermissions());

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

        plugin.log(logFormatter.formatMessage(this, senderName, msg, false));
    }

    public static void setDistance(int distance) {
        LocalChannel.distance = distance;
    }

    public static int getDistance() {
        return distance;
    }
}
