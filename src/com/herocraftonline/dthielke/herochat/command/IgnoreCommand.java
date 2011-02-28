package com.herocraftonline.dthielke.herochat.command;

import java.util.List;

import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.HeroChat.ChatColor;
import com.herocraftonline.dthielke.herochat.HeroChat.PluginPermission;

public class IgnoreCommand extends HeroChatCommand {

    public IgnoreCommand(HeroChat plugin) {
        super(plugin);

        this.name = "ignore";
        this.identifiers.add("ch ignore");
        this.identifiers.add("ignore");
    }

    private void displayIgnoreList(Player sender, List<String> ignoreList) {
        String ignoreListMsg;

        if (ignoreList.isEmpty()) {
            ignoreListMsg = "Currently ignoring no one.";
        } else {
            ignoreListMsg = "Currently ignoring: ";

            for (String s : ignoreList) {
                ignoreListMsg += s + ",";
            }
            ignoreListMsg = ignoreListMsg.substring(0, ignoreListMsg.length() - 1);
        }

        sender.sendMessage(ignoreListMsg);
    }

    @Override
    public void execute(Player sender, String[] args) {
        if (args.length > 1) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ignore OR /ignore <player>");
            return;
        }

        if (args[0].equalsIgnoreCase(sender.getName())) {
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You can't ignore yourself!");
            return;
        }

        List<String> ignoreList = plugin.getIgnoreList(sender);

        if (args[0].isEmpty())
            displayIgnoreList(sender, ignoreList);
        else
            toggleIgnore(sender, args[0].toLowerCase(), ignoreList);
    }

    private void toggleIgnore(Player sender, String name, List<String> ignoreList) {
        if (plugin.hasPermission(name, PluginPermission.ADMIN)) {
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "You can't ignore admins");
            return;
        }

        int index = -1;

        for (int i = 0; i < ignoreList.size(); i++) {
            if (ignoreList.get(i).equalsIgnoreCase(name)) {
                index = i;
                break;
            }
        }

        if (index != -1) {
            ignoreList.remove(index);

            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "No longer ignoring " + name);
        } else if (plugin.getServer().getPlayer(name) != null) {
            ignoreList.add(name);

            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Now ignoring " + name);
        } else {
            sender.sendMessage(ChatColor.ROSE.format() + plugin.getPluginTag() + "Player not found");
        }
    }

}
