package com.bukkit.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

public interface Executable {

    public void execute(PlayerChatEvent event, Player sender, String[] args);

}
