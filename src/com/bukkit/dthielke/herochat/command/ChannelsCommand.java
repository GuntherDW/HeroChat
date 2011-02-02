package com.bukkit.dthielke.herochat.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.dthielke.herochat.Channel;
import com.bukkit.dthielke.herochat.HeroChatPlugin;
import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;

public class ChannelsCommand extends Command {

    public static final int CHANNELS_PER_PAGE = 9;

    public ChannelsCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "channels";
        this.identifiers.add("/ch channels");
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {
        event.setCancelled(true);

        if (args.length > 1) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch channels [page#]");
            return;
        }

        List<Channel> channels = extractVisibleChannels(plugin.getChannels());

        int pages = (int) Math.ceil((double) channels.size() / CHANNELS_PER_PAGE);
        int p;

        if (args[0].isEmpty()) {
            p = 1;
        } else {
            try {
                p = Integer.parseInt(args[0]);
            } catch (NumberFormatException e) {
                sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch channels [page#]");
                return;
            }
        }

        if (p > pages)
            p = pages;

        sender.sendMessage(ChatColor.YELLOW.format() + "HeroChat: Channel list <Page " + p + "/" + pages + ">");

        for (int i = 0; i < CHANNELS_PER_PAGE; i++) {

            int index = (p - 1) * CHANNELS_PER_PAGE + i;

            if (index >= channels.size())
                break;

            Channel c = channels.get(index);

            String msg = c.getColorString() + "[" + c.getNick() + "] " + c.getName();
            if (c.hasPlayer(sender)) {
                msg = msg.concat(" *");
            }

            sender.sendMessage(msg);
        }
    }

    private List<Channel> extractVisibleChannels(List<Channel> channels) {
        List<Channel> visible = new ArrayList<Channel>();

        for (Channel c : channels)
            if (!c.isHidden())
                visible.add(c);

        return visible;
    }

}
