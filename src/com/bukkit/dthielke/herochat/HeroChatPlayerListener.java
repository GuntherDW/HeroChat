package com.bukkit.dthielke.herochat;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

import com.bukkit.dthielke.herochat.command.Command;
import com.nijikokun.bukkit.Permissions.Permissions;

public class HeroChatPlayerListener extends PlayerListener {

    private HeroChatPlugin plugin;

    public HeroChatPlayerListener(HeroChatPlugin plugin) {
        this.plugin = plugin;
    }

    public void onPlayerChat(PlayerChatEvent event) {
        Player sender = event.getPlayer();

        Channel c = plugin.getActiveChannel(sender);

        if (c == null)
            c = plugin.getDefaultChannel();

        c.sendMessage(sender, event.getMessage());

        event.setCancelled(true);
    }

    public void onPlayerCommand(PlayerChatEvent event) {
        Player sender = event.getPlayer();
        String message = event.getMessage();

        List<Command> commands = plugin.getCommands();
        Command bestMatch = null;
        int valid = -1;

        for (Command c : commands) {
            int tmpValid = c.validate(message);
            if (tmpValid != -1) {
                if (bestMatch == null) {
                    bestMatch = c;
                    valid = tmpValid;
                } else if (c.getIdentifiers().get(tmpValid).length() > bestMatch.getIdentifiers().get(valid).length()) {
                    bestMatch = c;
                    valid = tmpValid;
                }
            }
        }

        if (bestMatch != null) {
            String[] args = message.substring(bestMatch.getIdentifiers().get(valid).length()).trim().split(" ");

            bestMatch.execute(event, sender, args);
        }
    }

    public void onPlayerJoin(PlayerEvent event) {
        Player joiner = event.getPlayer();

        for (Channel c : plugin.getChannels()) {
            if (plugin.isUsingPermissions() && !c.getWhiteList().isEmpty()) {
                String group = Permissions.Security.getGroup(joiner.getName());

                if (!c.getWhiteList().contains(group))
                    continue;
            }

            if (c.isAutomaticallyJoined() || plugin.checkPlayerAutoJoinChannel(joiner.getName(), c))
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
