package com.herocraftonline.dthielke.herochat.command;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.herocraftonline.dthielke.herochat.Channel;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin;
import com.herocraftonline.dthielke.herochat.Channel.BanResult;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin.ChatColor;

public class BanCommand extends Command {

    public BanCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "ban";
        this.identifiers.add("/ch ban");
    }

    private void ban(Player sender, String name, Channel channel) {
        BanResult result = channel.banPlayer(sender, name);

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
        case SUCCESS:
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Banned player " + name + " from " + channel.getColoredName());
            plugin.getServer().getPlayer(name)
                    .sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Banned from " + channel.getColoredName() + ChatColor.ROSE.format() + " by " + sender.getName());
        }
    }

    private void displayBanList(Player sender, List<String> banList, Channel channel) {
        String banListMsg;

        if (banList.isEmpty()) {
            banListMsg = "No one is currently banned from " + channel.getColoredName();
        } else {
            banListMsg = "Currently banned from " + channel.getColoredName() + ChatColor.ROSE.format() + ": ";

            for (String s : banList) {
                banListMsg += s + ",";
            }
            banListMsg = banListMsg.substring(0, banListMsg.length() - 1);
        }

        sender.sendMessage(banListMsg);
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {

        event.setCancelled(true);

        if (args.length > 2 || args[0].isEmpty()) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch ban <channel> or /ch ban <channel> <player>");
            return;
        }

        Channel c = plugin.getChannel(args[0]);

        if (c != null) {
            List<String> banList = c.getBanList();

            if (args.length == 1)
                displayBanList(sender, banList, c);
            else
                toggleBan(sender, args[1], c);

        } else {
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Channel not found");
        }

    }

    private void toggleBan(Player sender, String name, Channel channel) {
        if (!channel.isBanned(name))
            ban(sender, name, channel);
        else
            unban(sender, name, channel);
    }

    private void unban(Player sender, String name, Channel channel) {
        BanResult result = channel.unbanPlayer(sender, name);

        switch (result) {
        case NO_PERMISSION:
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You are not a moderator of this channel");
            break;
        case SUCCESS:
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Unbanned player " + name + " from " + channel.getColoredName());
            plugin.getServer().getPlayer(name)
                    .sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Unbanned from " + channel.getColoredName() + ChatColor.ROSE.format() + "by " + sender.getName());
        }
    }

}
