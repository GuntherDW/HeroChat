/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.herocraftonline.dthielke.herochat.command.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.channels.Channel;
import com.herocraftonline.dthielke.herochat.channels.ChannelManager;
import com.herocraftonline.dthielke.herochat.command.BaseCommand;

public class RemoveCommand extends BaseCommand {

    public RemoveCommand(HeroChat plugin) {
        super(plugin);
        name = "Remove";
        description = "Removes a command";
        usage = "Usage: /ch remove <channel>";
        minArgs = 1;
        maxArgs = 1;
        identifiers.add("ch remove");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            ChannelManager cm = plugin.getChannelManager();
            Channel c = cm.getChannel(args[0]);
            if (c != null) {
                if (plugin.getPermissions().isAdmin(player) || c.getModerators().contains(player.getName())) {
                    if (cm.getChannels().size() > 1) {
                        if (!c.equals(cm.getDefaultChannel())) {
                            String[] players = cm.getPlayerList();
                            for (String s : players) {
                                if (cm.getActiveChannel(s).equals(c)) {
                                    List<Channel> joined = cm.getJoinedChannels(s);
                                    cm.setActiveChannel(s, joined.get(0).getName());
                                    plugin.log("Setting " + s + "'s active channel to " + joined.get(0).getName());
                                    Player p = plugin.getServer().getPlayer(s);
                                    if (p != null) {
                                        p.sendMessage(plugin.getTag() + "Set active channel to " + cm.getActiveChannel(s).getCName());
                                    }
                                }
                            }
                            cm.removeChannel(c);
                            sender.sendMessage(plugin.getTag() + "Channel " + c.getCName() + " §fremoved");
                            plugin.getConfigManager().save();
                        } else {
                            sender.sendMessage(plugin.getTag() + "You cannot delete the default channel");
                        }
                    } else {
                        sender.sendMessage(plugin.getTag() + "You cannot delete the last channel");
                    }
                } else {
                    sender.sendMessage(plugin.getTag() + "You do not have sufficient permission");
                }
            } else {
                sender.sendMessage(plugin.getTag() + "Channel not found");
            }
        } else {
            sender.sendMessage(plugin.getTag() + "You must be a player to use this command");
        }
    }

}
