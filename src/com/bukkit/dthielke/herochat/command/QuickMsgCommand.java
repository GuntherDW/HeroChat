package com.bukkit.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.dthielke.herochat.Channel;
import com.bukkit.dthielke.herochat.HeroChatPlugin;

public class QuickMsgCommand extends Command {

    public QuickMsgCommand(HeroChatPlugin plugin) {
        super(plugin);
        
        this.name = "quickmsg";
        this.identifier = "/";
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {
        if (args.length < 2)
            return;
        
        Channel c = plugin.getChannel(args[0]);
        
        if (c == null || !c.isQuickMessagable())
            return;
        
        event.setCancelled(true);
        
        boolean joined = c.addPlayer(sender);
        if (joined)
            sender.sendMessage("HeroChat: Joined channel " + c.getColoredName());
        
        String msg = "";
        for (int i = 1; i < args.length; i++) {
            msg += args[i] + " ";
        }
        msg = msg.trim();
        
        c.sendMessage(sender, msg);
    }

}
