package com.herocraftonline.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.herocraftonline.dthielke.herochat.Channel;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin.ChatColor;

public class JoinCommand extends Command {

    public JoinCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "join";
        this.identifiers.add("/ch join");
        this.identifiers.add("/join");
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {
        event.setCancelled(true);

        if (args.length != 1 || args[0].isEmpty()) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /join <channel>");
            return;
        }

        Channel c = plugin.getChannel(args[0]);

        if (c != null) {

            if (c.isBanned(sender)) {
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You are banned from " + c.getColoredName());
                return;
            }

            if (!c.getWhiteList().isEmpty()) {
                String group = plugin.security.getGroup(sender.getName());
                if (!c.getWhiteList().contains(group)) {
                    sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You are not allowed to join this channel");
                    return;
                }
            }

            boolean success = c.addPlayer(sender);

            if (success) {
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Joined channel " + c.getColoredName());
            } else {
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You are already in this channel");
            }

        } else {
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Channel not found");
        }
    }

}
