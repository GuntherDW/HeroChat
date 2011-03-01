package com.herocraftonline.dthielke.herochat.command;

import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.HeroChat.ChatColor;
import com.herocraftonline.dthielke.herochat.HeroChat.PluginPermission;

public class ReloadCommand extends HeroChatCommand {

    public ReloadCommand(HeroChat plugin) {
        super(plugin);

        this.name = "reload";
        this.identifiers.add("ch reload");
    }

    @Override
    public void execute(Player sender, String[] args) {
        if (!plugin.hasPermission(sender, PluginPermission.ADMIN)) {
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You must be an admin to reload HeroChatPlugin");
            return;
        }
        
        plugin.reload();

        sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Plugin reloaded");

    }

}
