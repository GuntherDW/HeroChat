package com.bukkit.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.dthielke.herochat.HeroChatPlugin;

public abstract class Command implements Executable {

    protected HeroChatPlugin plugin;
    
    protected String name;
    protected String identifier;

    public Command(HeroChatPlugin plugin) {
        this.plugin = plugin;
    }
    
    public String getName() {
        return name;
    }

    public String getIdentifier() {
        return identifier;
    }

    public boolean validate(String cmd) {
        return cmd.toLowerCase().startsWith(identifier);
    }

    public abstract void execute(PlayerChatEvent event, Player sender, String[] args);

}
