package com.bukkit.dthielke.herochat;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

import com.bukkit.dthielke.herochat.command.Command;

public class HeroChatPlayerListener extends PlayerListener {

    private HeroChatPlugin plugin;

    public HeroChatPlayerListener(HeroChatPlugin plugin) {
        this.plugin = plugin;
    }

    public void onPlayerCommand(PlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();

        List<Command> commands = plugin.getCommands();
        Command bestMatch = null;

        for (Command c : commands) {
            if (c.validate(message)) {
                if (bestMatch == null)
                    bestMatch = c;
                else if (c.getIdentifier().length() > bestMatch.getIdentifier().length())
                    bestMatch = c;
            }
        }

        if (bestMatch != null) {
            String[] args = message.substring(bestMatch.getIdentifier().length()).trim().split(" ");

            bestMatch.execute(event, sender, args);
        }
    }

    public void onPlayerChat(PlayerChatEvent event) {
        Player sender = event.getPlayer();

        Channel c = plugin.getActiveChannel(sender);

        if (c == null)
            c = plugin.getDefaultChannel();

        c.sendMessage(sender, event.getMessage());

        event.setCancelled(true);
    }

    public void onPlayerJoin(PlayerEvent event) {
        Player joiner = event.getPlayer();

        for (Channel c : plugin.getChannels()) {
            if (c.isAutomaticallyJoined())
                c.addPlayer(joiner);
        }

        plugin.setActiveChannel(joiner, plugin.getDefaultChannel());
        plugin.createIgnoreList(joiner);
    }

    public void onPlayerQuit(PlayerEvent event) {
        Player quitter = event.getPlayer();

        for (Channel c : plugin.getChannels()) {
            c.removePlayer(quitter);
        }

        plugin.getActiveChannelMap().remove(quitter);
        plugin.getIgnoreMap().remove(quitter);
    }

}
