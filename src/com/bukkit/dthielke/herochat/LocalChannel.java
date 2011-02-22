package com.bukkit.dthielke.herochat;

import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;
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
        if (plugin.isUsingPermissions() && !voiceList.isEmpty()) {
            String group = Permissions.Security.getGroup(sender.getName());

            if (!voiceList.contains(group)) {
                sender.sendMessage("HeroChat: You cannot speak in this channel");
                return;
            }
        }

        msg = plugin.censor(msg);

        List<String> msgLines = formatter.formatMessageWrapped(this, sender.getName(), sender.getDisplayName(), msg, plugin.getHealthBar(sender), plugin.isUsingPermissions());

        boolean heard = false;
        
        String sName = sender.getName();
        Location sLoc = plugin.getServer().getPlayer(sName).getLocation();
        
        //plugin.getServer().broadcastMessage("Speaker: (" + sLoc.getBlockX() + ", " + sLoc.getBlockZ() + ")");
        
        for (Player p : players) {
            if (!plugin.getIgnoreList(p).contains(sName)) {
                Location pLoc = p.getLocation();
                
                int dx = sLoc.getBlockX() - pLoc.getBlockX();
                int dz = sLoc.getBlockZ() - pLoc.getBlockZ();
                dx = dx * dx;
                dz = dz * dz;
                int d = (int)Math.sqrt(dx + dz);
                
                //plugin.getServer().broadcastMessage("Listener: (" + pLoc.getBlockX() + ", " + pLoc.getBlockZ() + "), d: " + d);
                
                if (d <= distance) {
                    for (String line : msgLines)
                        p.sendMessage(line);
        
                    if (!p.getName().equalsIgnoreCase(sName))
                        heard = true;
                }
            }
        }
        
        //if (heard)
            //plugin.getServer().broadcastMessage("Speaker is heard.");
        
        if (!heard)
            sender.sendMessage(ChatColor.GRAY.format() + "No one hears you.");

        plugin.log(LOG_FORMATTER.formatMessage(this, sName, sender.getDisplayName(), msg, plugin.getHealthBar(sender), false));
    }
}
