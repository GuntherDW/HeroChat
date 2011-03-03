package com.herocraftonline.dthielke.herochat.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.bukkit.entity.Player;

import com.herocraftonline.dthielke.herochat.HeroChat;
import com.herocraftonline.dthielke.herochat.HeroChat.ChatColor;
import com.herocraftonline.dthielke.herochat.channels.Channel;

public class Messaging {
    private static final String FONT_NAME = "minecraft.ttf";
    private static final int CHAT_LINE_LENGTH = 920;
    private static final String[] HEALTH_COLORS = { "§0", "§4", "§6", "§e" , "§2"};
    private static FontMetrics fontMetrics;

    public static List<String> formatWrapped(HeroChat plugin, Channel channel, String format, String name, String msg, boolean sentByPlayer) {
        if (fontMetrics == null) {
            createFontMetrics();
        }

        String leader = createLeader(plugin, channel, format, name, msg, sentByPlayer);
        List<String> msgLines = wrap(leader + msg, fontMetrics);

        String firstLine = msgLines.get(0);
        int colorIndex = firstLine.lastIndexOf("\u00a7");
        String lastColor = firstLine.substring(colorIndex, colorIndex + 2);

        List<String> coloredLines = new ArrayList<String>();
        for (int i = 0; i < msgLines.size(); i++) {
            coloredLines.add(lastColor + msgLines.get(i));
        }

        return coloredLines;
    }

    public static String format(HeroChat plugin, Channel channel, String format, String name, String msg, boolean sentByPlayer) {
        String leader = createLeader(plugin, channel, format, name, msg, sentByPlayer);
        return leader + msg;
    }

    private static String createLeader(HeroChat plugin, Channel channel, String format, String name, String msg, boolean sentByPlayer) {
        String prefix = "";
        String suffix = "";
        String world = "";
        String healthBar = "";
        if (sentByPlayer) {
            try {
                Player sender = plugin.getServer().getPlayer(name);
                prefix = plugin.getPermissions().getPrefix(sender);
                suffix = plugin.getPermissions().getSuffix(sender);
                world = sender.getWorld().getName();
                name = sender.getDisplayName();
                healthBar = createHealthBar(sender);
            } catch (Exception e) {
                plugin.log("Error encountered while fetching prefixes/suffixes from Permissions. Is Permissions properly configured and up to date?");
            }
        }

        String leader = format;
        leader = leader.replaceAll("\\{default\\}", plugin.getChannelManager().getDefaultMsgFormat());
        leader = leader.replaceAll("\\{prefix\\}", prefix);
        leader = leader.replaceAll("\\{suffix\\}", suffix);
        leader = leader.replaceAll("\\{nick\\}", channel.getNick());
        leader = leader.replaceAll("\\{name\\}", channel.getName());
        leader = leader.replaceAll("\\{player\\}", name);
        leader = leader.replaceAll("\\{healthbar\\}", healthBar);
        leader = leader.replaceAll("\\{color.CHANNEL\\}", channel.getColor().str);
        leader = leader.replaceAll("\\{world\\}", world);

        Matcher matcher = Pattern.compile("\\{color.[a-zA-Z]+\\}").matcher(leader);
        while (matcher.find()) {
            String match = matcher.group();
            String colorString = match.substring(7, match.length() - 1);
            leader = leader.replaceAll("\\Q" + match + "\\E", ChatColor.valueOf(colorString).str);
        }

        return leader;
    }

    private static List<String> wrap(String msg, FontMetrics fontMetrics) {
        msg = msg.replace(" ", "  ");
        ArrayList<String> lines = new ArrayList<String>();

        while (!msg.isEmpty()) {
            boolean flag = false;

            for (int i = 0; i < msg.length(); i++) {
                String tmpLine = msg.substring(0, i + 1).replaceAll("\u00a7[0-9a-f]", "");
                if (fontMetrics.stringWidth(tmpLine) >= CHAT_LINE_LENGTH) {
                    lines.add(msg.substring(0, i));
                    msg = msg.substring(i);
                    flag = true;
                    break;
                }
            }

            if (!flag) {
                lines.add(msg);
                break;
            }
        }

        ArrayList<String> out = new ArrayList<String>();
        for (String s : lines) {
            out.add(s.replace("  ", " "));
        }

        return out;
    }

    private static void createFontMetrics() {
        Graphics dummyGraphics = new BufferedImage(2, 2, BufferedImage.TYPE_4BYTE_ABGR_PRE).createGraphics();

        try {
            InputStream is = Messaging.class.getResourceAsStream(FONT_NAME);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(24f);

            is.close();

            fontMetrics = dummyGraphics.getFontMetrics(font);
        } catch (Exception ex) {
            ex.printStackTrace();
        }
    }
    
    private static String createHealthBar(Player player) {
        int health = player.getHealth();
        int fullBars = health / 4;
        int remainder = health % 4;
        String healthBar = "";
        for (int i = 0; i < fullBars; i++) {
            healthBar += HEALTH_COLORS[4] + "|";
        }
        int barsLeft = 5 - fullBars;
        if (barsLeft > 0) {
            healthBar += HEALTH_COLORS[remainder] + "|";
            barsLeft--;
            for (int i = 0; i < barsLeft; i++) {
                healthBar += HEALTH_COLORS[0] + "|";
            }
        }
        return healthBar;
    }
}
