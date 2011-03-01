package com.herocraftonline.dthielke.herochat.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.channels.Channel;
import com.herocraftonline.dthielke.herochat.command.BaseCommand;

public class ModCommand extends BaseCommand {

    public ModCommand(HeroChat plugin) {
        super(plugin);
        name = "Mod";
        description = "Grants moderator privileges to a player";
        usage = "Usage: /ch mod <channel> <player>";
        minArgs = 2;
        maxArgs = 2;
        identifiers.add("ch mod");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String name = player.getName();
            Channel c = plugin.getChannelManager().getChannel(args[0]);
            if (c != null) {
                if (c.getModerators().contains(name) || plugin.getPermissions().isAdmin(player)) {
                    Player mod = plugin.getServer().getPlayer(args[1]);
                    if (mod != null) {
                        if (!c.getModerators().contains(mod.getName())) {
                            c.getModerators().add(mod.getName());
                            sender.sendMessage(plugin.getTag() + mod.getName() + " is now moderating " + c.getCName());
                            mod.sendMessage(plugin.getTag() + "You are now moderating " + c.getCName());
                        } else {
                            sender.sendMessage(plugin.getTag() + mod.getName() + " is already moderating " + c.getCName());
                        }
                    } else {
                        sender.sendMessage(plugin.getTag() + "Player not found");
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
