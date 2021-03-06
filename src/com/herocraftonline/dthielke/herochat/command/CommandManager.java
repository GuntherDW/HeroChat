/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.herocraftonline.dthielke.herochat.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;

public class CommandManager {

    protected List<BaseCommand> commands;

    public CommandManager() {
        commands = new ArrayList<BaseCommand>();
    }

    public boolean dispatch(CommandSender sender, Command command, String label, String[] args) {
        String input = label + " ";
        for (String s : args) {
            input += s + " ";
        }

        BaseCommand match = null;
        String[] trimmedArgs = null;
        StringBuilder identifier = new StringBuilder();

        for (BaseCommand cmd : commands) {
            StringBuilder tmpIdentifier = new StringBuilder();
            String[] tmpArgs = cmd.validate(input, tmpIdentifier);
            if (tmpArgs != null && tmpIdentifier.length() != 0) {
                if (tmpIdentifier.length() > identifier.length()) {
                    identifier = tmpIdentifier;
                    trimmedArgs = tmpArgs;
                    match = cmd;
                }
            }
        }

        if (match != null) {
            match.execute(sender, trimmedArgs);
            return true;
        }

        return false;
    }

    public void addCommand(BaseCommand command) {
        commands.add(command);
    }

    public void removeCommand(BaseCommand command) {
        commands.remove(command);
    }
}
