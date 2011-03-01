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
        
        System.out.println("CMD: " + identifier);

        return false;
    }
    
    public void addCommand(BaseCommand command) {
        commands.add(command);
    }
    
    public void removeCommand(BaseCommand command) {
        commands.remove(command);
    }
}
