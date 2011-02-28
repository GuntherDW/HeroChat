package com.herocraftonline.dthielke.herochat.command;

import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.Channel;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin.ChatColor;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin.PluginPermission;

public class RemoveCommand extends HeroChatCommand {

    public RemoveCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "remove";
        this.identifiers.add("ch remove");
    }

    @Override
    public void execute(Player sender, String[] args) {
        if (args.length > 1 || args[0].isEmpty()) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch remove <name>");
            return;
        }

        Channel c = plugin.getChannel(args[0]);

        if (c != null) {
            
            if (!plugin.hasPermission(sender, PluginPermission.REMOVE) && !c.isModerator(sender)) {
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You do not have permission to remove this channel");
                return;
            }

            if (c.isPermanent() && !plugin.hasPermission(sender, PluginPermission.ADMIN)) {
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Channel " + c.getColoredName() + ChatColor.ROSE.format() + " is permanent and cannot be removed");
                return;
            }

            if (c == plugin.getDefaultChannel())
                plugin.setDefaultChannel(plugin.getChannels().get(0));

            Player[] players = c.getPlayers().toArray(new Player[0]);
            for (Player p : players) {
                p.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Left channel " + c.getColoredName());
                c.removePlayer(p);
            }
            
            plugin.getChannels().remove(c);
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Removed channel " + c.getColoredName());
            
            if (c.isSaved())
                plugin.saveConfig();

        } else {
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Channel not found");
        }
    }

}
