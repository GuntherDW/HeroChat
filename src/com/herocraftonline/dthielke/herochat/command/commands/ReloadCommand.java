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
import com.herocraftonline.dthielke.herochat.command.BaseCommand;

public class ReloadCommand extends BaseCommand {

    public ReloadCommand(HeroChat plugin) {
        super(plugin);
        name = "Reload";
        description = "Reloads the plugin";
        usage = "Usage: /ch reload";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("ch reload");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            if (plugin.getPermissions().isAdmin(player)) {
                // plugin.getPluginLoader().disablePlugin(plugin);
                plugin.onEnable();
                // plugin.getConfigManager().reload();
                sender.sendMessage(plugin.getTag() + "Plugin reloaded");
            } else {
                sender.sendMessage(plugin.getTag() + "You do not have sufficient permission");
            }
        } else {
            sender.sendMessage(plugin.getTag() + "You must be a player to use this command");
        }
    }

}
