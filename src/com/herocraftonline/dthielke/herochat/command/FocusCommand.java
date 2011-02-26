package com.herocraftonline.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.herocraftonline.dthielke.herochat.Channel;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin.ChatColor;

public class FocusCommand extends Command {

    public FocusCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "focus";
        this.identifiers.add("/ch");
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {

        event.setCancelled(true);

        if (args[0].isEmpty()) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch <channel>");
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

            boolean joined = c.addPlayer(sender);
            if (joined) {
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Joined channel " + c.getColoredName());
            }

            plugin.setActiveChannel(sender, c);
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Set active channel to " + c.getColoredName());
            
        } else {
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Channel not found");
        }
    }

}
