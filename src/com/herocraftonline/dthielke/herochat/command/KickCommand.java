package com.herocraftonline.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.herocraftonline.dthielke.herochat.Channel;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin;
import com.herocraftonline.dthielke.herochat.Channel.KickResult;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin.ChatColor;

public class KickCommand extends Command {

    public KickCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "kick";
        this.identifiers.add("/ch kick");
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {

        event.setCancelled(true);

        if (args.length != 2) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch kick <channel> <player>");
            return;
        }

        Channel c = plugin.getChannel(args[0]);

        if (c != null) {
            KickResult result = c.kickPlayer(sender, args[1]);

            switch (result) {
            case NO_PERMISSION:
                sender.sendMessage("HeroChat: You are not a moderator of this channel");
                break;
            case PLAYER_IS_ADMIN:
                sender.sendMessage("HeroChat: You cannot kick admins");
                break;
            case PLAYER_IS_MODERATOR:
                sender.sendMessage("HeroChat: You cannot kick moderators");
                break;
            case PLAYER_NOT_FOUND:
                sender.sendMessage("HeroChat: Player not found");
                break;
            case SUCCESS:
                sender.sendMessage("HeroChat: Kicked player " + args[1] + " from " + c.getColoredName());
                plugin.getServer().getPlayer(args[1])
                        .sendMessage("HeroChat: Kicked from " + c.getColoredName() + ChatColor.WHITE.format() + " by " + sender.getName());
            }
        } else {
            sender.sendMessage("HeroChat: Channel not found");
        }

    }

}
