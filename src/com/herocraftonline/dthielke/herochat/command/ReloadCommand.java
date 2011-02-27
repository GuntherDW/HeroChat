package com.herocraftonline.dthielke.herochat.command;

import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChatPlugin;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin.ChatColor;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin.PluginPermission;

public class ReloadCommand extends HeroChatCommand {

    public ReloadCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "reload";
        this.identifiers.add("ch reload");
    }

    @Override
    public void execute(Player sender, String[] args) {
        if (!plugin.hasPermission(sender, PluginPermission.ADMIN)) {
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You must be an admin to reload HeroChat");
            return;
        }
        
        plugin.reload();

        sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Plugin reloaded");

    }

}
