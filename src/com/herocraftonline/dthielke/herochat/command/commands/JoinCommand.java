/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

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
        usage = "Usage: /ch join <channel> [password]";
        minArgs = 1;
        maxArgs = 2;
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
            String password = "";
            if(args.length>1)
                password = args[1];
            if(plugin.getPermissions().isAdmin(player))
            {
                if(args.length>1 && args[1].equals("force"))
                    password = c.getPassword();
            }
            
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
                        if(c.getPassword() != null && !c.getPassword().equals("") && !c.getPassword().equals(password))
                        {
                            sender.sendMessage(plugin.getTag() + "Incorrect password for " + c.getCName());
                            if(plugin.getPermissions().isAdmin(player)) {
                                sender.sendMessage(plugin.getTag() + "Admin note:");
                                sender.sendMessage(plugin.getTag() + "Force the join by using 'force' as the password!");
                            }
                        } else {
                            c.addPlayer(name);
                            sender.sendMessage(plugin.getTag() + "Joined channel " + c.getCName());
                        }
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
