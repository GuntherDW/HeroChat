package com.herocraftonline.dthielke.herochat.experimental;

import java.util.List;

import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.command.Executable;

public abstract class BaseCommand implements Executable {

    protected HeroChat plugin;
    protected final String name;
    protected final String description;
    protected final String usage;
    protected final int minArgs;
    protected final int maxArgs;
    protected List<String> identifiers;
    
    public BaseCommand(HeroChat plugin) {
        this.plugin = plugin;
        this.name = "Base";
        this.description = "Abstract base command class";
        this.usage = "Usage";
        this.minArgs = 0;
        this.maxArgs = 0;
    }
    
    public abstract void execute(Player sender, String[] args);
    
    public int isValid(String command, String[] args) {
        command = command.toLowerCase();
        int validIdentifier = -1;
        boolean validArgs = false;
        
        // check number of arguments
        int a = args.length;
        if (a >= minArgs && a <= maxArgs)
            validArgs = true;
        
        // look for a matching identifier
        int n = identifiers.size();
        for (int i = 0; i < n; i++) {
            String identifier = identifiers.get(i).toLowerCase();
            if (command.startsWith(identifier)) {
                validIdentifier = i;
            }
        }
        return validArgs ? validIdentifier : -1;
    }

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public void setIdentifiers(List<String> identifiers) {
        this.identifiers = identifiers;
    }

    public String getName() {
        return name;
    }

}
