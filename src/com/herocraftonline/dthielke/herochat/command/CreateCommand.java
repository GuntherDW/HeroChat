package com.herocraftonline.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.herocraftonline.dthielke.herochat.Channel;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin.ChatColor;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin.PluginPermission;
import com.herocraftonline.dthielke.herochat.util.MessageFormatter;

public class CreateCommand extends Command {

    public CreateCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "create";
        this.identifiers.add("/ch create");
    }

    private Channel createChannel(String[] args, boolean full) {
        Channel c = new Channel(plugin);

        c.setName(args[0]);
        c.setNick(args[1]);

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

        c.setFormatter(new MessageFormatter(plugin, "{default}"));

        return c;
    }

    private void applyOptions(Channel c, char[] args, boolean full) {
        for (char option : args) {
            switch (option) {
            case 'h':
                c.setHidden(true);
                break;
            case 'j':
                c.setJoinMessages(true);
                break;
            case 's':
                c.setSaved(true);
                break;
            case 'a':
                if (full)
                    c.setAutomaticallyJoined(true);
                break;
            case 'q':
                if (full)
                    c.setQuickMessagable(true);
                break;
            case 'f':
                if (full)
                    c.setForced(true);
                break;
            case 'p':
                if (full)
                    c.setPermanent(true);
                break;
            }
        }
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {
        event.setCancelled(true);

        if (args.length < 2 || args.length > 4) {
            sender.sendMessage(ChatColor.ROSE.format() + "Invalid syntax. Type /ch help create for info.");
            return;
        }

        if (!plugin.hasPermission(sender, PluginPermission.CREATE)) {
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You do not have permission to create channels");
            return;
        }

        Channel c = createChannel(args, plugin.hasPermission(sender, PluginPermission.ADMIN));

        if (c == null) {
            sender.sendMessage(ChatColor.ROSE.format() + "Invalid syntax. Type /ch help create for info.");
            return;
        }

        for (String reserved : HeroChatPlugin.RESERVED_NAMES) {
            if (args[0].equalsIgnoreCase(reserved)) {
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "That name is reserved");
                return;
            }

            if (args[1].equalsIgnoreCase(reserved)) {
                sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "That nick is reserved");
                return;
            }
        }

        Channel nameTest = plugin.getChannel(c.getName());
        Channel nickTest = plugin.getChannel(c.getNick());

        if (nameTest != null) {
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "That name is taken");
            return;
        }

        if (nickTest != null) {
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "That nick is taken");
            return;
        }

        plugin.getChannels().add(c);
        sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Created channel " + c.getColoredName());

        c.addModerator(sender);
        sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You are now moderating " + c.getColoredName());

        c.addPlayer(sender);
        sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Joined channel " + c.getColoredName());

        if (c.isSaved())
            plugin.saveConfig();
    }

}
