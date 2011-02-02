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

    public static final String FONT_NAME = "minecraft.ttf";
    public static final int CHAT_LINE_LENGTH = 940;

    private static String defaultMessageFormat = "";

    public static String colorToString(ChatColor color) {
        return "\u00a7" + Integer.toHexString(color.ordinal());
    }

    public static String getDefaultMessageFormat() {
        return defaultMessageFormat;
    }

    public static void setDefaultMessageFormat(String defaultMessageFormat) {
        MessageFormatter.defaultMessageFormat = defaultMessageFormat;
    }

    public static List<String> wrap(String msg, FontMetrics fontMetrics) {

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

    private FontMetrics fontMetrics;

    private String format;

    public MessageFormatter() {
        createFontMetrics();

        this.format = "{default}";
    }

    public MessageFormatter(String format) {
        createFontMetrics();

        this.format = format;
    }

    private String applyFormat(Channel channel, String prefix, String suffix, String displayName, String healthBar) {
        healthBar = healthBar.replaceAll("&", "\u00a7");

        String leader = format;

        leader = leader.replaceAll("\\{default\\}", defaultMessageFormat);

        leader = leader.replaceAll("\\{nick\\}", channel.getNick());
        leader = leader.replaceAll("\\{name\\}", channel.getName());
        leader = leader.replaceAll("\\{prefix\\}", prefix);
        leader = leader.replaceAll("\\{suffix\\}", suffix);
        leader = leader.replaceAll("\\{player\\}", displayName);
        leader = leader.replaceAll("\\{healthbar\\}", healthBar);
        leader = leader.replaceAll("\\{color.CHANNEL\\}", channel.getColorString());

        Matcher matcher = Pattern.compile("\\{color.[a-zA-Z]+\\}").matcher(leader);
        while (matcher.find()) {
            String match = matcher.group();
            String colorString = match.substring(7, match.length() - 1);
            leader = leader.replaceAll("\\Q" + match + "\\E", ChatColor.valueOf(colorString).format());
        }

        return leader;
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

    private String createLeader(Channel channel, String name, String displayName, String msg, String healthBar, boolean usePermissions) {
        String prefix = "";
        String suffix = "";

        if (usePermissions) {
            prefix = Permissions.Security.getUserPermissionString(name, "prefix");
            suffix = Permissions.Security.getUserPermissionString(name, "suffix");

            String group = Permissions.Security.getGroup(name);
            if (prefix.equals(""))
                prefix = Permissions.Security.getGroupPrefix(group);

            if (suffix.equals(""))
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

        return applyFormat(channel, prefix, suffix, displayName, healthBar);
    }

    public String formatMessage(Channel channel, String name, String displayName, String msg, String healthBar, boolean usePermissions) {
        String leader = createLeader(channel, name, displayName, msg, healthBar, usePermissions);

        return leader + msg;
    }

    public List<String> formatMessageWrapped(Channel channel, String name, String displayName, String msg, String healthBar, boolean usePermissions) {
        String leader = createLeader(channel, name, displayName, msg, healthBar, usePermissions);

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

    public String getFormat() {
        return format;
    }

    public void setFormat(String format) {
        this.format = format;
    }
}
