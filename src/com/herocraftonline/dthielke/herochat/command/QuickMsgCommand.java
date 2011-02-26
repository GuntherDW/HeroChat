package com.herocraftonline.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.herocraftonline.dthielke.herochat.Channel;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin.ChatColor;

public class QuickMsgCommand extends Command {

    public QuickMsgCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "quickmsg";
        this.identifiers.add("/");
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {
        if (args.length < 2)
            return;

        Channel c = plugin.getChannel(args[0]);

        if (c == null || !c.isQuickMessagable())
            return;
        
        event.setCancelled(true);
        
        if (c.isBanned(sender)) {
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You are banned from " + c.getColoredName());
            return;
        }

        if (!c.getWhiteList().isEmpty()) {
            String group = plugin.security.getGroup(sender.getName());
            
            if (!c.getWhiteList().contains(group)) {
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You are not allowed to join this channel");
                return;
            }
        }
        
        boolean joined = c.addPlayer(sender);
        if (joined)
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Joined channel " + c.getColoredName());

        String msg = "";
        for (int i = 1; i < args.length; i++) {
            msg += args[i] + " ";
        }
        msg = msg.trim();

        c.sendMessage(sender, msg);
    }

}
