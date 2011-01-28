package com.bukkit.dthielke.herochat.command;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.dthielke.herochat.Channel;
import com.bukkit.dthielke.herochat.HeroChatPlugin;
import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;

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
            if (c.isModerator(p))
                playerList += p.getName() + "*, ";
            else
                playerList += p.getName() + ", ";
        }
        playerList = playerList.substring(0, playerList.length() - 2);
        
        sender.sendMessage(playerList);
    }

}
