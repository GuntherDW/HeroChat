package com.herocraftonline.dthielke.herochat.command;

import org.bukkit.entity.Player;

public interface Executable {

    public void execute(Player sender, String[] args);

}
