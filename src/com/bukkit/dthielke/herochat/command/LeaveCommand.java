package com.bukkit.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.dthielke.herochat.Channel;
import com.bukkit.dthielke.herochat.HeroChatPlugin;
import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;

public class LeaveCommand extends Command {

    public LeaveCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "leave";
        this.identifier = "/leave";
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {
        event.setCancelled(true);

        if (args.length != 1 || args[0].isEmpty()) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /leave <channel>");
            return;
        }

        Channel c = plugin.getChannel(args[0]);

        if (c != null) {

            if (c.isForced()) {
                sender.sendMessage("HeroChat: You cannot leave " + c.getColoredName());
                return;
            }

            boolean success = c.removePlayer(sender);

            if (success)
                sender.sendMessage("HeroChat: Left channel " + c.getColoredName());
            else
                sender.sendMessage("HeroChat: You are not in this channel");

        } else {
            sender.sendMessage("HeroChat: Channel not found");
        }
    }

}
