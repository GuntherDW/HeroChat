package com.herocraftonline.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.herocraftonline.dthielke.herochat.HeroChatPlugin;
import com.herocraftonline.dthielke.herochat.HeroChatPlugin.ChatColor;

public class HelpCommand extends Command {

    private static final String[] HELP_ONE = { ChatColor.YELLOW.format() + "HeroChat Help <Page 1/2>:",
                                              "/ch help [page#] " + ChatColor.LIGHT_GRAY.format() + "- displays this menu",
                                              "/ch <channel> " + ChatColor.LIGHT_GRAY.format() + "- sets your active channel",
                                              "/ch list " + ChatColor.LIGHT_GRAY.format() + "- lists players in your active channel",
                                              "/ch channels " + ChatColor.LIGHT_GRAY.format() + "- lists publicly available channels",
                                              "/join <channel> " + ChatColor.LIGHT_GRAY.format() + "- joins a channel",
                                              "/leave <channel> " + ChatColor.LIGHT_GRAY.format() + "- leaves a channel",
                                              "/<channel> <msg> " + ChatColor.LIGHT_GRAY.format() + "- sends a message to a quick message",
                                              "/ignore " + ChatColor.LIGHT_GRAY.format() + "- displays your ignore list",
                                              "/ignore <player> " + ChatColor.LIGHT_GRAY.format() + "- toggles ignoring a player" };

    private static final String[] HELP_TWO = { ChatColor.YELLOW.format() + "HeroChat Help <Page 2/2>:",
                                              "/ch create <name> <nick> [color:#] [-options]",
                                              "/ch remove <channel>",
                                              "/ch mod <channel> <player> " + ChatColor.LIGHT_GRAY.format() + "- grants mod privileges",
                                              "/ch kick <channel> <player> " + ChatColor.LIGHT_GRAY.format() + "- kicks a player",
                                              "/ch ban <channel> <player> " + ChatColor.LIGHT_GRAY.format() + "- toggles banning a player",
                                              "/ch reload " + ChatColor.LIGHT_GRAY.format() + "- reloads the config file" };
    private static final String[] HELP_CREATE = { "Usage: /ch create <name> <nick> [color:#] [-options]",
                                                  "Options (combinable, ie. -hsqf):",
                                                  "-h   Hidden from /ch channels list",
                                                  "-j   Show join and leave messages",
                                                  "-s   Save the channel",
                                                  "Admin-only options:",
                                                  "-a   Automatically joined by new users",
                                                  "-q   Allow quick message shortcut",
                                                  "-f   Force users to stay in this channel",
                                                  "-p   Make this channel permanent (only removable via config)" };    

    public HelpCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "help";
        this.identifiers.add("/ch help");
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {

        event.setCancelled(true);

        if (args.length > 1) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch help [page#]");
            return;
        }

        String[] help;
        if (args[0].isEmpty() || args[0].equals("1"))
            help = HELP_ONE;
        else if (args[0].equals("2"))
            help = HELP_TWO;
        else if (args[0].equals("create"))
            help = HELP_CREATE;
        else
            help = HELP_ONE;
        
        if (help != HELP_CREATE)
            for (String s : help)
                sender.sendMessage(ChatColor.LIGHT_GREEN.format() + s.replace("HeroChat", plugin.getPluginTag()));
        else
            for (String s : help)
                sender.sendMessage(ChatColor.ROSE.format() + s);
    }

}