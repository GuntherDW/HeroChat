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
                                              "/ch create <name> <nick> [color:#] [hidden] [saved]",
                                              ChatColor.LIGHT_GRAY.format() + "  creates a channel (square brackets mean optional)",
                                              "/ch remove <channel> " + ChatColor.LIGHT_GRAY.format() + "- deletes a channel",
                                              "/ch mod <channel> <player> " + ChatColor.LIGHT_GRAY.format() + "- grants mod privileges",
                                              "/ch kick <channel> <player> " + ChatColor.LIGHT_GRAY.format() + "- kicks a player",
                                              "/ch ban <channel> <player> " + ChatColor.LIGHT_GRAY.format() + "- toggles banning a player",
                                              "/ch reload " + ChatColor.LIGHT_GRAY.format() + "- reloads the config file" };

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
        else
            help = HELP_TWO;

        for (String s : help)
            sender.sendMessage(ChatColor.LIGHT_GREEN.format() + s);
    }

}