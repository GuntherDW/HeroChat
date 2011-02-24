package com.herocraftonline.dthielke.herochat;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChatPlugin.ChatColor;
import com.nijikokun.bukkit.Permissions.Permissions;

public class LocalChannel extends Channel {

    public static final int DEFAULT_DISTANCE = 100;

    protected static int distance = DEFAULT_DISTANCE;

    public static int getDistance() {
        return distance;
    }

    public static void setDistance(int distance) {
        LocalChannel.distance = distance;
    }

    public LocalChannel(HeroChatPlugin plugin) {
        super(plugin);
    }

    @Override
    public void sendMessage(Player sender, String msg) {
        if (!voiceList.isEmpty()) {
            String group = Permissions.Security.getGroup(sender.getName());

            if (!voiceList.contains(group)) {
                sender.sendMessage("HeroChat: You cannot speak in this channel");
                return;
            }
        }

        msg = plugin.censor(msg);

        List<String> msgLines = formatter.formatMessageWrapped(this, sender.getWorld().getName(), sender.getName(), sender.getDisplayName(), msg, plugin.getHealthBar(sender));

        boolean heard = false;
        
        String sName = sender.getName();
        Location sLoc = plugin.getServer().getPlayer(sName).getLocation();
        
        for (Player p : players) {
            p = plugin.getServer().getPlayer(p.getName());
            if (p == null)
                continue;
            if (!plugin.getIgnoreList(p).contains(sName)) {
                Location pLoc = p.getLocation();
                
                int dx = sLoc.getBlockX() - pLoc.getBlockX();
                int dz = sLoc.getBlockZ() - pLoc.getBlockZ();
                dx = dx * dx;
                dz = dz * dz;
                int d = (int)Math.sqrt(dx + dz);
                
                if (d <= distance) {
                    for (String line : msgLines)
                        p.sendMessage(line);
        
                    if (!p.getName().equalsIgnoreCase(sName))
                        heard = true;
                }
            }
        }
        
        if (!heard)
            sender.sendMessage(ChatColor.GRAY.format() + "No one hears you.");

        plugin.log(logFormatter.formatMessage(this, sender.getWorld().getName(), sName, sender.getDisplayName(), msg, plugin.getHealthBar(sender)));
    }
}
