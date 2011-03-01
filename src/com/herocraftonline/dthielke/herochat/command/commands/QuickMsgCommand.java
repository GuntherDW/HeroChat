package com.herocraftonline.dthielke.herochat.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.channels.Channel;
import com.herocraftonline.dthielke.herochat.command.BaseCommand;

public class QuickMsgCommand extends BaseCommand {

    public QuickMsgCommand(HeroChat plugin) {
        super(plugin);
        name = "Quick Message";
        description = "Sends a message to a channel without changing focus";
        usage = "Usage: /qm <channel> <msg>";
        minArgs = 2;
        maxArgs = 1000;
        identifiers.add("qm");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String name = player.getName();
            Channel c = plugin.getChannelManager().getChannel(args[0]);
            if (c != null) {
                if (c.getPlayers().contains(name)) {
                    String group = plugin.getPermissions().getGroup(player);
                    if (c.getVoicelist().contains(group) || c.getVoicelist().isEmpty()) {
                        String msg = "";
                        for (int i = 1; i < args.length; i++) {
                            msg += args[i] + " ";
                        }
                        c.sendMessage(name, msg.trim());
                    } else {
                        sender.sendMessage(plugin.getTag() + "You cannot speak in " + c.getCName());
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
