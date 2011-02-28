package com.herocraftonline.dthielke.herochat.command;

import java.util.List;

import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.Channel;
import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.HeroChat.ChatColor;
import com.herocraftonline.dthielke.herochat.HeroChat.PluginPermission;

public class ListCommand extends HeroChatCommand {

    public ListCommand(HeroChat plugin) {
        super(plugin);

        this.name = "list";
        this.identifiers.add("ch list");
    }

    @Override
    public void execute(Player sender, String[] args) {
        if (!args[0].isEmpty()) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch list");
            return;
        }

        Channel c = plugin.getActiveChannel(sender);
        List<Player> players = c.getPlayers();

        String playerList = "Currently in " + c.getColoredName() + ChatColor.WHITE.format() + ": ";
        for (Player p : players) {
            String name = p.getName();

            if (plugin.hasPermission(p, PluginPermission.ADMIN))
                name = "@" + name;

            if (c.isModerator(p))
                name += "*";

            name += ", ";

            playerList += name;
        }
        playerList = playerList.substring(0, playerList.length() - 2);

        sender.sendMessage(playerList);
    }

}
