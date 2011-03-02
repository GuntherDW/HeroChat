package com.herocraftonline.dthielke.herochat.channels;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.util.Messaging;

public class LocalChannel extends Channel {

    protected int distance;

    public LocalChannel(HeroChat plugin) {
        super(plugin);
    }

    public void sendMessage(String name, String msg) {
        Player sender = plugin.getServer().getPlayer(name);
        if (sender != null) {
            List<Player> receivers = getListeners(sender);
            List<String> formattedMsg = Messaging.formatWrapped(plugin, this, msgFormat, name, msg);
            for (Player receiver : receivers) {
                for (String line : formattedMsg) {
                    receiver.sendMessage(line);
                }
            }
            
            if (receivers.size() == 1) {
                sender.sendMessage("§8No one hears you.");
            }
        }
        String logMsg = Messaging.format(plugin, this, logFormat, name, msg);
        plugin.log(logMsg);
    }

    private List<Player> getListeners(Player origin) {
        List<Player> list = new ArrayList<Player>();
        Location sLoc = origin.getLocation();
        for (String name : players) {
            Player player = plugin.getServer().getPlayer(name);
            if (player != null) {
                if (!plugin.getChannelManager().isIgnoring(name, origin.getName())) {
                    Location pLoc = player.getLocation();

                    int dx = sLoc.getBlockX() - pLoc.getBlockX();
                    int dz = sLoc.getBlockZ() - pLoc.getBlockZ();
                    dx = dx * dx;
                    dz = dz * dz;
                    int d = (int) Math.sqrt(dx + dz);

                    if (d <= distance) {
                        list.add(player);
                    }
                }
            }
        }
        return list;
    }

    public int getDistance() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance = distance;
    }

}
