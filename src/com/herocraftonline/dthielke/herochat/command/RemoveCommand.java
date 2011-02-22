package com.herocraftonline.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.herocraftonline.dthielke.herochat.Channel;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin.ChatColor;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin.PluginPermission;

public class RemoveCommand extends Command {

    public RemoveCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "remove";
        this.identifiers.add("/ch remove");
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {
        event.setCancelled(true);

        if (args.length > 1 || args[0].isEmpty()) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch remove <name>");
            return;
        }

        Channel c = plugin.getChannel(args[0]);

        if (c != null) {
            
            if (!plugin.hasPermission(sender, PluginPermission.REMOVE) && !c.isModerator(sender)) {
                sender.sendMessage("HeroChat: You do not have permission to remove this channel");
                return;
            }

            if (c.isPermanent() && !plugin.hasPermission(sender, PluginPermission.ADMIN)) {
                sender.sendMessage("HeroChat: Channel " + c.getColoredName() + ChatColor.WHITE.format() + "is permanent and cannot be removed");
                return;
            }

            plugin.getChannels().remove(c);

            if (c.isSaved())
                plugin.saveConfigOld();

            sender.sendMessage("HeroChat: Removed channel " + c.getColoredName());

            if (c == plugin.getDefaultChannel())
                plugin.setDefaultChannel(plugin.getChannels().get(0));

            for (Player p : c.getPlayers()) {
                plugin.setActiveChannel(p, plugin.getDefaultChannel());

                p.sendMessage("HeroChat: Left channel " + c.getColoredName());
                p.sendMessage("HeroChat: Set active channel to " + plugin.getDefaultChannel().getColoredName());
            }

        } else {
            sender.sendMessage("HeroChat: Channel not found");
        }
    }

}
