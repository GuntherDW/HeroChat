package com.herocraftonline.dthielke.herochat;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

public class HeroChatPlayerListener extends PlayerListener {

    private HeroChat plugin;

    public HeroChatPlayerListener(HeroChat plugin) {
        this.plugin = plugin;
    }

    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled())
            return;
        
        Player sender = event.getPlayer();

        Channel c = plugin.getActiveChannel(sender);

        if (c == null) {
            c = plugin.getDefaultChannel();
            c.addPlayer(sender);
        }

        c.sendMessage(sender, event.getMessage());

        event.setCancelled(true);
    }

    public void onPlayerJoin(PlayerEvent event) {        
        Player joiner = event.getPlayer();

        plugin.checkNewPlayerSettings(joiner);

        for (Channel c : plugin.getJoinedChannels(joiner))
            c.addPlayer(joiner);

        plugin.createIgnoreList(joiner);
    }

    public void onPlayerQuit(PlayerEvent event) {
        Player quitter = event.getPlayer();

        List<Channel> channels = plugin.getJoinedChannels(quitter);
        for (Channel c : channels)
            c.getPlayers().remove(quitter);

        plugin.getIgnoreMap().remove(quitter);
        plugin.savePlayerSettings(quitter.getName());
        //plugin.savePlayerSettings();
    }

}
