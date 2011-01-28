package com.bukkit.dthielke.herochat.util;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.bukkit.dthielke.herochat.Channel;
import com.bukkit.dthielke.herochat.HeroChatPlugin.ChatColor;
import com.nijikokun.bukkit.Permissions.Permissions;

public class MessageFormatter {

    private static final String DEFAULT_FORMAT = "{color.CHANNEL}[{nick}] {color.WHITE}{prefix}{player}{suffix}{color.CHANNEL}: ";
    private static final String FONT_NAME = "minecraft.ttf";
    private static final int CHAT_LINE_LENGTH = 940;

    private FontMetrics fontMetrics;
    private String format;

    public MessageFormatter() {
        createFontMetrics();
        
        this.format = DEFAULT_FORMAT;
    }
    
    public MessageFormatter(String format) {
        createFontMetrics();
        
        this.format = format;
    }

    private void createFontMetrics() {

        Graphics dummyGraphics = new BufferedImage(2, 2, BufferedImage.TYPE_BYTE_BINARY).createGraphics();

        try {
            InputStream is = MessageFormatter.class.getResourceAsStream(FONT_NAME);
            Font font = Font.createFont(Font.TRUETYPE_FONT, is).deriveFont(24f);

            is.close();

            fontMetrics = dummyGraphics.getFontMetrics(font);
        } catch (Exception ex) {
            ex.printStackTrace();
        }

    }

    public List<String> formatMessageWrapped(Channel channel, String sender, String msg, boolean usePermissions) {
        String leader = createLeader(channel, sender, msg, usePermissions);
        
        List<String> msgLines = wrapMessage(leader + msg, fontMetrics);
        List<String> coloredLines = new ArrayList<String>();
        for (int i = 0; i < msgLines.size(); i++) {
            coloredLines.add(channel.getColorString() + msgLines.get(i));
        }

        return coloredLines;
    }
    
    public String formatMessage(Channel channel, String sender, String msg, boolean usePermissions) {
        String leader = createLeader(channel, sender, msg, usePermissions);

        return leader + msg;
    }
    
    private String createLeader(Channel channel, String sender, String msg, boolean usePermissions) {
        String prefix = "";
        String suffix = "";

        if (usePermissions) {
            String group = Permissions.Security.getGroup(sender);
            prefix = Permissions.Security.getGroupPrefix(group);
            suffix = Permissions.Security.getGroupSuffix(group);
        }

        if (prefix == null) {
            prefix = "";
        } else {
            prefix = prefix.replace("&", "\u00a7");
        }

        if (suffix == null) {
            suffix = "";
        } else {
            suffix = suffix.replace("&", "\u00a7");
        }
        
        return applyFormat(channel, prefix, suffix, sender);
    }
    
    private String applyFormat(Channel channel, String prefix, String suffix, String sender) {
        String leader = format;
        
        leader = leader.replaceAll("\\{nick\\}", channel.getNick());
        leader = leader.replaceAll("\\{name\\}", channel.getName());
        leader = leader.replaceAll("\\{prefix\\}", prefix);
        leader = leader.replaceAll("\\{suffix\\}", suffix);
        leader = leader.replaceAll("\\{player\\}", sender);
        leader = leader.replaceAll("\\{color.CHANNEL\\}", channel.getColorString());
        
        
        Matcher matcher = Pattern.compile("\\{color.[a-zA-Z]+\\}").matcher(leader);
        while (matcher.find()) {
            String match = matcher.group();
            String colorString = match.substring(7, match.length() - 1);
            leader = leader.replaceAll("\\Q" + match + "\\E", ChatColor.valueOf(colorString).format());
        }
        
        return leader;
    }

    private static List<String> wrapMessage(String msg, FontMetrics fontMetrics) {

        msg = msg.replaceAll(" ", "  ");
        ArrayList<String> lines = new ArrayList<String>();

        while (!msg.isEmpty()) {
            boolean flag = false;

            for (int i = 0; i < msg.length(); i++) {
                String tmpLine = msg.substring(0, i + 1).replaceAll("\u00a7[0-9a-f]", "");
                if (fontMetrics.stringWidth(tmpLine) > CHAT_LINE_LENGTH) {
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
            out.add(s.replaceAll("  ", " "));
        }

        return out;
    }

    public static String colorToString(ChatColor color) {
        return "\u00a7" + Integer.toHexString(color.ordinal());
    }

}
