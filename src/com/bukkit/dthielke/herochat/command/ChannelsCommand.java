package com.bukkit.dthielke.herochat.command;

import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.dthielke.herochat.Channel;
import com.bukkit.dthielke.herochat.HeroChatPlugin;
import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;

public class ChannelsCommand extends Command {

    public ChannelsCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "channels";
        this.identifier = "/ch channels";
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {
        event.setCancelled(true);

        if (!args[0].isEmpty()) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch channels");
            return;
        }
        
        sender.sendMessage("HeroChat: Channel list");

        List<Channel> channels = plugin.getChannels();

        for (Channel c : channels) {
            if (!c.isHidden()) {

                String msg = c.getColorString() + "[" + c.getNick() + "] " + c.getName();
                if (c.hasPlayer(sender)) {
                    msg = msg.concat(" *");
                }
                
                sender.sendMessage(msg);

            }
        }
    }

}
