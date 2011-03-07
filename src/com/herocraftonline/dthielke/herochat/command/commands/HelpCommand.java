/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.herocraftonline.dthielke.herochat.command.commands;

import org.bukkit.command.CommandSender;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.command.BaseCommand;

public class HelpCommand extends BaseCommand {

    private static final String[] HELP_ONE = { "§eHeroChat Help <Page 1/2>:", "/ch help [page#] §7- displays this menu",
                                              "/ch <channel> §7- sets your active channel", "/ch who §7- lists players in your active channel",
                                              "/ch list §7- lists publicly available channels", "/join <channel> §7- joins a channel",
                                              "/leave <channel> §7- leaves a channel", "/<channel> <msg> §7- sends a quick message to a channel",
                                              "/ch ignore §7- displays your ignore list", "/ch ignore <player> §7- toggles ignoring a player" };
    private static final String[] HELP_TWO = { "§eHeroChat Help <Page 2/2>:", "/ch create <name> <nick> [color:#] [-options]", "/ch remove <channel>",
                                              "/ch mod <channel> <player> §7- grants mod privileges", "/ch kick <channel> <player> §7- kicks a player",
                                              "/ch ban <channel> <player> §7- toggles banning a player", "/ch reload §7- reloads the config file" };
    private static final String[] HELP_CREATE = { "Usage: /ch create <name> <nick> [color:#] [-options]", "Options (combinable, ie. -hsqf):",
                                                 "-h   Hidden from /ch channels list", "-j   Show join and leave messages", "Admin-only options:",
                                                 "-a   Automatically joined by new users", "-q   Allow quick message shortcut",
                                                 "-f   Force users to stay in this channel" };

    public HelpCommand(HeroChat plugin) {
        super(plugin);
        name = "Help";
        description = "Displays the help menu";
        usage = "Usage: /ch help [page#]";
        minArgs = 0;
        maxArgs = 1;
        identifiers.add("ch help");
    }

    @Override
    public void execute(CommandSender sender, String[] args) {
        String[] help;
        if (args.length == 0 || args[0].equals("1")) {
            help = HELP_ONE;
        } else if (args[0].equals("2")) {
            help = HELP_TWO;
        } else if (args[0].equals("create")) {
            help = HELP_CREATE;
        } else {
            help = HELP_ONE;
        }

        if (help != HELP_CREATE) {
            for (String s : help) {
                sender.sendMessage("§a" + s.replace("HeroChat", plugin.getTag()));
            }
        } else {
            for (String s : help) {
                sender.sendMessage("§c" + s);
            }
        }
    }

}
