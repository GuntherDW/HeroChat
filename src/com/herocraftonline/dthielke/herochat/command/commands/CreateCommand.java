package com.herocraftonline.dthielke.herochat.command.commands;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.HeroChat.ChatColor;
import com.herocraftonline.dthielke.herochat.channels.Channel;
import com.herocraftonline.dthielke.herochat.channels.ChannelManager;
import com.herocraftonline.dthielke.herochat.command.BaseCommand;

public class CreateCommand extends BaseCommand {

    private static final String[] RESERVED_NAMES = {};

    public CreateCommand(HeroChat plugin) {
        super(plugin);
        name = "Create";
        description = "Creates a channel. Type /ch help create for info";
        usage = "Usage: /ch create <name> <nick> [color:#] [-options]";
        minArgs = 2;
        maxArgs = 4;
        identifiers.add("ch create");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        ChannelManager cm = plugin.getChannelManager();
        if (sender instanceof Player) {
            Player creator = (Player) sender;
            if (plugin.getPermissions().canCreate(creator)) {
                for (String reserved : RESERVED_NAMES) {
                    if (args[0].equalsIgnoreCase(reserved)) {
                        sender.sendMessage(plugin.getTag() + "That name is reserved");
                        return;
                    } else if (args[1].equalsIgnoreCase(reserved)) {
                        sender.sendMessage(plugin.getTag() + "That nick is reserved");
                        return;
                    }
                }
                if (cm.getChannel(args[0]) != null) {
                    sender.sendMessage(plugin.getTag() + "That name is taken");
                    return;
                } else if (cm.getChannel(args[1]) != null) {
                    sender.sendMessage(plugin.getTag() + "That nick is taken");
                    return;
                }
                Channel c = createChannel(args, plugin.getPermissions().isAdmin(creator));
                if (c != null) {
                    String name = creator.getName();
                    c.getModerators().add(name);
                    c.addPlayer(name);
                    cm.addChannel(c);
                    cm.setActiveChannel(name, c.getName());
                    sender.sendMessage(plugin.getTag() + "Created channel " + c.getCName());
                    plugin.getConfigManager().save();
                } else {
                    sender.sendMessage(plugin.getTag() + "Invalid syntax. Type /ch help create for info");
                }
            } else {
                sender.sendMessage(plugin.getTag() + "You cannot create channels");
            }
        } else {
            sender.sendMessage(plugin.getTag() + "You must be a player to create channels");
        }
    }

    private Channel createChannel(String[] args, boolean full) {
        Channel c = new Channel(plugin);
        c.setName(args[0]);
        c.setNick(args[1]);
        c.setMsgFormat("{default}");
        for (int i = 2; i < args.length; i++) {
            String tmp = args[i].toLowerCase();

            if (tmp.startsWith("color:")) {
                try {
                    int color = Integer.parseInt(tmp.substring(6), 16);
                    c.setColor(ChatColor.values()[color]);
                } catch (NumberFormatException e) {
                    return null;
                }
            } else if (tmp.startsWith("-")) {
                tmp = tmp.substring(1);
                applyOptions(c, tmp.toCharArray(), full);
            }
        }
        return c;
    }

    private void applyOptions(Channel c, char[] args, boolean full) {
        for (char option : args) {
            switch (option) {
            case 'h':
                c.setHidden(true);
                break;
            case 'j':
                c.setVerbose(true);
                break;
            case 'a':
                if (full) {
                    c.setAutoJoined(true);
                }
                break;
            case 'q':
                if (full) {
                    c.setQuickMessagable(true);
                }
                break;
            case 'f':
                if (full) {
                    c.setForced(true);
                }
                break;
            }
        }
    }

}
