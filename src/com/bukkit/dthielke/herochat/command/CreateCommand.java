package com.bukkit.dthielke.herochat.command;

import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;

import com.bukkit.dthielke.herochat.Channel;
import com.bukkit.dthielke.herochat.HeroChatPlugin;
import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;
import com.bukkit.dthielke.herochat.HeroChatPlugin.PluginPermission;

public class CreateCommand extends Command {

    public CreateCommand(HeroChatPlugin plugin) {
        super(plugin);

        this.name = "create";
        this.identifier = "/ch create";
    }

    @Override
    public void execute(PlayerChatEvent event, Player sender, String[] args) {
        event.setCancelled(true);
        
        if (args.length < 2 || args.length > 5) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch create <name> <nick> [color:#] [hidden] [saved]");
            return;
        }
        
        if (!plugin.hasPermission(sender, PluginPermission.CREATE)) {
            sender.sendMessage("HeroChat: You do not have permission to create channels");
            return;
        }

        Channel c = createChannel(args);
        
        if (c == null) {
            sender.sendMessage(ChatColor.ROSE.format() + "Usage: /ch create <name> <nick> [color:#] [hidden] [saved]");
            return;
        }
        
        for (String reserved : HeroChatPlugin.RESERVED_NAMES) {
            if (args[0].equalsIgnoreCase(reserved)) {
                sender.sendMessage("HeroChat: That name is reserved");
                return;
            }
            
            if (args[1].equalsIgnoreCase(reserved)) {
                sender.sendMessage("HeroChat: That nick is reserved");
                return;
            }
        }
        
        Channel nameTest = plugin.getChannel(c.getName());
        Channel nickTest = plugin.getChannel(c.getNick());
        
        if (nameTest != null) {
            sender.sendMessage("HeroChat: That name is taken");
            return;
        }
        
        if (nickTest != null) {
            sender.sendMessage("HeroChat: That nick is taken");
            return;
        }
        
        plugin.getChannels().add(c);
        sender.sendMessage("HeroChat: Created channel " + c.getColoredName());
        
        c.addModerator(sender);
        sender.sendMessage("HeroChat: You are now moderating " + c.getColoredName());
        
        c.addPlayer(sender);
        sender.sendMessage("HeroChat: Joined channel " + c.getColoredName());
    }
    
    private Channel createChannel(String[] args) {
        Channel c = new Channel(plugin);
        
        c.setName(args[0]);
        c.setNick(args[1]);
        
        for (int i = 2; i < args.length; i++) {
            String tmp = args[i].toLowerCase();
            
            if (tmp.startsWith("color:")) {
                try {
                    int color = Integer.parseInt(tmp.substring(6));
                    c.setColor(ChatColor.values()[color]);
                } catch(NumberFormatException e) {
                    return null;
                }
            } else if (tmp.equals("hidden")) {
                c.setHidden(true);
            } else if (tmp.equals("saved")) {
                c.setSaved(true);
            }
        }
        
        c.setFormatter(plugin.getRegularMessageFormatter());
        
        return c;
    }

}
