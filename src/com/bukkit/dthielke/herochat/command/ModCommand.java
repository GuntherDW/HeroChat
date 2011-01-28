package com.bukkit.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.dthielke.herochat.Channel;
import com.bukkit.dthielke.herochat.HeroChatPlugin;
import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;
import com.bukkit.dthielke.herochat.HeroChatPlugin.PluginPermission;

public class ModCommand extends Command {

    public ModCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "mod";
        this.identifier = "/ch mod";
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {
        
        event.setCancelled(true);
        
        if (args.length != 2) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch mod <channel> <player>");
            return;
        }
        
        Channel c = plugin.getChannel(args[0]);
        
        if (c != null) {
            if (!c.isModerator(sender) && !plugin.hasPermission(sender, PluginPermission.ADMIN)) {
                sender.sendMessage("HeroChat: You are not a moderator of this channel");
                return;
            }
            
            if (c.isModerator(args[1])) {
                sender.sendMessage("HeroChat: " + args[1] + " is already moderating this channel");
                return;
            }
            
            Player newMod = plugin.getServer().getPlayer(args[1]);
            
            if (newMod == null) {
                sender.sendMessage("HeroChat: Player not found");
                return;
            }
            
            c.addModerator(newMod);
            sender.sendMessage("HeroChat: Granted " + args[1] + " moderation priveleges of " + c.getColoredName());
            newMod.sendMessage("HeroChat: You are now moderating " + c.getColoredName());
            
        } else {
            sender.sendMessage("HeroChat: Channel not found");
        }
    }

}
