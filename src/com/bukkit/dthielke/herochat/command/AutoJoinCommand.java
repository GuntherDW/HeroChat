package com.bukkit.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.dthielke.herochat.Channel;
import com.bukkit.dthielke.herochat.HeroChatPlugin;
import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;

public class AutoJoinCommand extends Command {

    public AutoJoinCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "auto";
        this.identifiers.add("/ch auto");
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {
        event.setCancelled(true);
        
        if (args[0].isEmpty()) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch auto <channel>");
            return;
        }
        
        Channel c = plugin.getChannel(args[0]);
        
        if (c != null) {
            if (c.isAutomaticallyJoined()) {
                sender.sendMessage("HeroChat: This channel must be set to auto-join");
                return;
            }
            
            
            boolean auto = plugin.toggleAutoJoin(sender.getName(), c);
            
            if (auto)
                sender.sendMessage("HeroChat: Now auto-joining " + c.getColoredName());
            else
                sender.sendMessage("HeroChat: No longer auto-joining " + c.getColoredName());
            
            plugin.saveConfig();
        } else {
            sender.sendMessage("HeroChat: Channel not found");
        }
    }

}
