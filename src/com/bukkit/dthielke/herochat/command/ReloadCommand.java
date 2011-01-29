package com.bukkit.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.dthielke.herochat.Channel;
import com.bukkit.dthielke.herochat.HeroChatPlugin;
import com.bukkit.dthielke.herochat.HeroChatPlugin.PluginPermission;
import com.nijikokun.bukkit.Permissions.Permissions;

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

        plugin.loadConfig();

        for (Channel c : plugin.getChannels()) {
            if (c.isAutomaticallyJoined()) {
                for (Player p : plugin.getServer().getOnlinePlayers()) {
                    if (plugin.isUsingPermissions() && !c.getWhiteList().isEmpty()) {
                        String group = Permissions.Security.getGroup(p.getName());
                        
                        if (c.getWhiteList().contains(group)) {                            
                            c.addPlayer(p);

                            p.sendMessage("HeroChat: Joined channel " + c.getColoredName());
                        }
                    } else {
                        c.addPlayer(p);

                        p.sendMessage("HeroChat: Joined channel " + c.getColoredName());
                    }
                }
            }
        }

        for (Player p : plugin.getServer().getOnlinePlayers()) {
            plugin.setActiveChannel(p, plugin.getDefaultChannel());

            p.sendMessage("HeroChat: Set active channel to " + plugin.getDefaultChannel().getColoredName());
        }

        sender.sendMessage("HeroChat: Plugin reloaded");

    }

}
