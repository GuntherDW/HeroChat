package com.herocraftonline.dthielke.herochat.command;

import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.Channel;
import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.Channel.KickResult;
import com.herocraftonline.dthielke.herochat.HeroChat.ChatColor;

public class KickCommand extends HeroChatCommand {

    public KickCommand(HeroChat plugin) {
        super(plugin);

        this.name = "kick";
        this.identifiers.add("ch kick");
    }

    @Override
    public void execute(Player sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch kick <channel> <player>");
            return;
        }

        Channel c = plugin.getChannel(args[0]);

        if (c != null) {
            KickResult result = c.kickPlayer(sender, args[1]);

            switch (result) {
            case NO_PERMISSION:
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You are not a moderator of this channel");
                break;
            case PLAYER_IS_ADMIN:
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You cannot kick admins");
                break;
            case PLAYER_IS_MODERATOR:
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You cannot kick moderators");
                break;
            case PLAYER_NOT_FOUND:
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Player not found");
                break;
            case SUCCESS:
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Kicked player " + args[1] + " from " + c.getColoredName());
                plugin.getServer().getPlayer(args[1])
                        .sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Kicked from " + c.getColoredName() + ChatColor.ROSE.format() + " by " + sender.getName());
            }
        } else {
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Channel not found");
        }

    }

}
