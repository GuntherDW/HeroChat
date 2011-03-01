package com.herocraftonline.dthielke.herochat.command;

import java.util.ArrayList;
import java.util.List;

import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;

public abstract class HeroChatCommand implements Executable {

    protected HeroChat plugin;

    protected String name;
    protected List<String> identifiers;

    public HeroChatCommand(HeroChat plugin) {
        this.plugin = plugin;

        identifiers = new ArrayList<String>();
    }

    public abstract void execute(Player sender, String[] args);

    public List<String> getIdentifiers() {
        return identifiers;
    }

    public String getName() {
        return name;
    }

    public int validate(String cmd) {
        int valid = -1;
        cmd = cmd.toLowerCase();

        for (int i = 0; i < identifiers.size(); i++) {
            if (cmd.startsWith(identifiers.get(i).toLowerCase())) {
                valid = i;
            }
        }

        return valid;
    }

}
