package com.bukkit.dthielke.herochat.command;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.dthielke.herochat.Channel;
import com.bukkit.dthielke.herochat.HeroChatPlugin;
import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;
import com.bukkit.dthielke.herochat.HeroChatPlugin.PluginPermission;

public class ListCommand extends Command {

    public ListCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "list";
        this.identifier = "/ch list";
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {
        event.setCancelled(true);

        if (!args[0].isEmpty()) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch list");
            return;
        }

        Channel c = plugin.getActiveChannel(sender);
        List<Player> players = c.getPlayers();

        String playerList = "Currently in " + c.getColoredName() + ChatColor.WHITE.format() + ": ";
        for (Player p : players) {
            String name = p.getName();

            if (plugin.hasPermission(sender, PluginPermission.ADMIN))
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
