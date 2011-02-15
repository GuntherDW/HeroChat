package com.bukkit.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.dthielke.herochat.HeroChatPlugin;
import com.bukkit.dthielke.herochat.HeroChatPlugin.PluginPermission;

public class ReloadCommand extends Command {

    public ReloadCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "reload";
        this.identifiers.add("/ch reload");
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {

        event.setCancelled(true);

        if (!plugin.hasPermission(sender, PluginPermission.ADMIN)) {
            sender.sendMessage("HeroChat: You must be an admin to reload HeroChat");
            return;
        }
        
        plugin.onEnable();

//        plugin.loadConfigOld();
//
//        plugin.joinAllDefaultChannels(true);
//
//        for (Player p : plugin.getServer().getOnlinePlayers()) {
//            plugin.setActiveChannel(p, plugin.getDefaultChannel());
//
//            p.sendMessage("HeroChat: Set active channel to " + plugin.getDefaultChannel().getColoredName());
//        }

        sender.sendMessage("HeroChat: Plugin reloaded");

    }

}
