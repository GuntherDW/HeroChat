package com.herocraftonline.dthielke.herochat.command;

import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.Channel;
import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.HeroChat.ChatColor;
import com.herocraftonline.dthielke.herochat.HeroChat.PluginPermission;

public class ModCommand extends HeroChatCommand {

    public ModCommand(HeroChat plugin) {
        super(plugin);

        this.name = "mod";
        this.identifiers.add("ch mod");
    }

    @Override
    public void execute(Player sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch mod <channel> <player>");
            return;
        }

        Channel c = plugin.getChannel(args[0]);

        if (c != null) {
            if (!c.isModerator(sender) && !plugin.hasPermission(sender, PluginPermission.ADMIN)) {
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You are not a moderator of this channel");
                return;
            }

            if (c.isModerator(args[1])) {
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + args[1] + " is already moderating this channel");
                return;
            }

            Player newMod = plugin.getServer().getPlayer(args[1]);

            if (newMod == null) {
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Player not found");
                return;
            }

            c.addModerator(newMod);
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Granted " + args[1] + " moderation priveleges of " + c.getColoredName());
            newMod.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You are now moderating " + c.getColoredName());

        } else {
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Channel not found");
        }
    }

}
