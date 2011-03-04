/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.herocraftonline.dthielke.herochat;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerEvent;
import org.bukkit.event.player.PlayerListener;

import com.herocraftonline.dthielke.herochat.channels.Channel;
import com.herocraftonline.dthielke.herochat.channels.ChannelManager;

public class HeroChatPlayerListener extends PlayerListener {

    private HeroChat plugin;

    public HeroChatPlayerListener(HeroChat plugin) {
        this.plugin = plugin;
    }

    @Override
    public void onPlayerCommandPreprocess(PlayerChatEvent event) {
        String input = event.getMessage().substring(1);
        String[] args = input.split(" ");
        plugin.log(input);
        if (plugin.getChannelManager().getChannel(args[0]) != null) {
            plugin.log("valid channel");
            event.setCancelled(true);
            plugin.getCommandManager().dispatch(event.getPlayer(), null, "qm", args);
        }
    }

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if (event.isCancelled()) {
            return;
        }

        Player sender = event.getPlayer();
        String name = sender.getName();
        ChannelManager cm = plugin.getChannelManager();
        Channel c = cm.getActiveChannel(name);

        if (c != null) {
            String group = plugin.getPermissions().getGroup(sender);
            if (c.getVoicelist().contains(group) || c.getVoicelist().isEmpty()) {
                if (!c.getPlayers().contains(name)) {
                    c.addPlayer(name);
                }
                c.sendMessage(sender.getDisplayName(), event.getMessage());
            } else {
                sender.sendMessage(plugin.getTag() + "You cannot speak in this channel");
            }
        }
        event.setCancelled(true);
    }

    @Override
    public void onPlayerJoin(PlayerEvent event) {
        Player joiner = event.getPlayer();
        String name = joiner.getName();
        plugin.getConfigManager().loadPlayer(name);
    }

    @Override
    public void onPlayerQuit(PlayerEvent event) {
        Player quitter = event.getPlayer();
        String name = quitter.getName();
        plugin.getConfigManager().savePlayer(name);
        plugin.getChannelManager().removeFromAll(name);
    }

}
