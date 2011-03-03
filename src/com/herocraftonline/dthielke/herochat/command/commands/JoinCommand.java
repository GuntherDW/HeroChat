package com.herocraftonline.dthielke.herochat.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.channels.Channel;
import com.herocraftonline.dthielke.herochat.channels.ChannelManager;
import com.herocraftonline.dthielke.herochat.command.BaseCommand;

public class JoinCommand extends BaseCommand {

    public JoinCommand(HeroChat plugin) {
        super(plugin);
        name = "Join";
        description = "Joins a channel";
        usage = "Usage: /ch join <channel>";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("ch join");
        identifiers.add("join");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String name = player.getName();
            ChannelManager cm = plugin.getChannelManager();
            Channel c = cm.getChannel(args[0]);
            if (c != null) {
                if (!c.getBlacklist().contains(name)) {
                    if (!c.getWhitelist().isEmpty()) {
                        String group = plugin.getPermissions().getGroup(player);
                        if (!c.getWhitelist().contains(group)) {
                            sender.sendMessage(plugin.getTag() + "You cannot join this channel");
                            return;
                        }
                    }

                    if (!c.getPlayers().contains(name)) {
                        c.addPlayer(name);
                        sender.sendMessage(plugin.getTag() + "Joined channel " + c.getCName());
                    } else {
                        sender.sendMessage(plugin.getTag() + "You are already in " + c.getCName());
                    }
                } else {
                    sender.sendMessage(plugin.getTag() + "You are banned from " + c.getCName());
                }
            } else {
                sender.sendMessage(plugin.getTag() + "Channel not found");
            }
        } else {
            sender.sendMessage(plugin.getTag() + "You must be a player to use this command");
        }
    }

}
