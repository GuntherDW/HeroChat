package com.herocraftonline.dthielke.herochat.command.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.channels.Channel;
import com.herocraftonline.dthielke.herochat.channels.ChannelManager;
import com.herocraftonline.dthielke.herochat.command.BaseCommand;

public class LeaveCommand extends BaseCommand {

    public LeaveCommand(HeroChat plugin) {
        super(plugin);
        name = "Leave";
        description = "Leaves a channel";
        usage = "Usage: /ch <channel>";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("ch leave");
        identifiers.add("leave");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player joiner = (Player) sender;
            String name = joiner.getName();
            ChannelManager cm = plugin.getChannelManager();
            Channel c = cm.getChannel(args[0]);
            if (c != null) {
                if (c.getPlayers().contains(name)) {
                    if (!c.isForced()) {
                        c.removePlayer(name);
                        sender.sendMessage(plugin.getTag() + "Left channel " + c.getCName());
                        if (cm.getActiveChannel(name).equals(c)) {
                            List<Channel> joined = cm.getJoinedChannels(name);
                            cm.setActiveChannel(name, joined.get(0).getName());
                            sender.sendMessage(plugin.getTag() + "Set active channel to " + cm.getActiveChannel(name).getCName());
                        }
                    } else {
                        sender.sendMessage(plugin.getTag() + "You cannot leave " + c.getCName());
                    }
                } else {
                    sender.sendMessage(plugin.getTag() + "You are not in " + c.getCName());
                }
            } else {
                sender.sendMessage(plugin.getTag() + "Channel not found");
            }
        } else {
            sender.sendMessage(plugin.getTag() + "You must be a player to use this command");
        }
    }

}
