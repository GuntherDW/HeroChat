package com.herocraftonline.dthielke.herochat.command.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.channels.Channel;
import com.herocraftonline.dthielke.herochat.command.BaseCommand;

public class WhoCommand extends BaseCommand {

    public WhoCommand(HeroChat plugin) {
        super(plugin);
        name = "Who";
        description = "Lists all users in your active channel";
        usage = "Usage: /ch who";
        minArgs = 0;
        maxArgs = 0;
        identifiers.add("ch who");
    }

    // TODO: BUG! Players showing up twice.
    @Override
    public void execute(CommandSender sender, String[] args) {
        if (sender instanceof Player) {
            Player player = (Player) sender;
            String name = player.getName();
            Channel c = plugin.getChannelManager().getActiveChannel(name);
            if (c != null) {
                List<String> players = c.getPlayers();
                String playerList = "Currently in " + c.getCName() + "§f: ";
                for (String pName : players) {
                    Player p = plugin.getServer().getPlayer(pName);
                    if (p != null) {
                        if (plugin.getPermissions().isAdmin(p)) {
                            pName = "@" + pName;
                        } else if (c.getModerators().contains(pName)) {
                            pName += "*";
                        }
                        pName += ", ";
                        playerList += pName;
                    }
                }
                playerList = playerList.substring(0, playerList.length() - 2);
                sender.sendMessage(playerList);
            }
        } else {
            sender.sendMessage(plugin.getTag() + "You must be a player to use this command");
        }
    }

}
