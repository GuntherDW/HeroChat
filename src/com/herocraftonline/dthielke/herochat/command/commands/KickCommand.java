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

public class KickCommand extends BaseCommand {

    public KickCommand(HeroChat plugin) {
        super(plugin);
        name = "Kick";
        description = "Removes a player from a channel";
        usage = "Usage: /ch kick <channel> <player>";
        minArgs = 2;
        maxArgs = 2;
        identifiers.add("ch kick");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ChannelManager cm = plugin.getChannelManager();
        Channel channel = cm.getChannel(args[0]);
        if (channel != null) {
            if (sender instanceof Player) {
                Player kicker = (Player) sender;
                if (plugin.getPermissions().isAdmin(kicker) || channel.getModerators().contains(kicker.getName())) {
                    Player kickee = plugin.getServer().getPlayer(args[1]);
                    if (kickee != null) {
                        String name = kickee.getName();
                        if (!(plugin.getPermissions().isAdmin(kickee) || channel.getModerators().contains(name))) {
                            if (channel.getPlayers().contains(name)) {
                                channel.removePlayer(name);
                                kicker.sendMessage(plugin.getTag() + name + " has been kicked from " + channel.getCName());
                                kickee.sendMessage(plugin.getTag() + "You have been kicked from " + channel.getCName());
                                if (cm.getActiveChannel(name).equals(channel)) {
                                    List<Channel> joined = cm.getJoinedChannels(name);
                                    cm.setActiveChannel(name, joined.get(0).getName());
                                    kickee.sendMessage(plugin.getTag() + "Set active channel to " + cm.getActiveChannel(name).getCName());
                                }
                            } else {
                                kicker.sendMessage(plugin.getTag() + name + " is not in " + channel.getCName());
                            }
                        } else {
                            kicker.sendMessage(plugin.getTag() + "You cannot kick " + name + " from " + channel.getCName());
                        }
                    } else {
                        kicker.sendMessage(plugin.getTag() + "Player not found");
                    }
                } else {
                    kicker.sendMessage(plugin.getTag() + "You do not have sufficient permission");
                }
            } else {
                sender.sendMessage(plugin.getTag() + "You must be a player to use this command");
            }
        } else {
            sender.sendMessage(plugin.getTag() + "Channel not found");
        }
    }

}
