/**
 * Copyright (C) 2011 DThielke <dave.thielke@gmail.com>
 * 
 * This work is licensed under the Creative Commons Attribution-NonCommercial-NoDerivs 3.0 Unported License.
 * To view a copy of this license, visit http://creativecommons.org/licenses/by-nc-nd/3.0/ or send a letter to
 * Creative Commons, 171 Second Street, Suite 300, San Francisco, California, 94105, USA.
 **/

package com.herocraftonline.dthielke.herochat.util;

import org.bukkit.entity.Player;

import com.nijiko.permissions.PermissionHandler;

public class PermissionHelper {

    public static final int[] MIN_VERSION = { 2, 5, 1 };
    private PermissionHandler security;

    public PermissionHelper(PermissionHandler security) {
        this.security = security;
    }

    public String getGroup(Player p) {
        String world = p.getWorld().getName();
        String name = p.getName();
        return security.getGroup(world, name);
    }

    public String getPrefix(Player p) {
        try {
            String world = p.getWorld().getName();
            String name = p.getName();
            String prefix = security.getUserPermissionString(world, name, "prefix");
            if (prefix == null || prefix.isEmpty()) {
                String group = security.getGroup(world, name);
                prefix = security.getGroupPrefix(world, group);
                if (prefix == null) {
                    prefix = "";
                }
            }
            return prefix.replace(String.valueOf((char)194), "");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    public String getSuffix(Player p) {
        try {
            String world = p.getWorld().getName();
            String name = p.getName();
            String suffix = security.getUserPermissionString(world, name, "suffix");
            if (suffix == null || suffix.isEmpty()) {
                String group = security.getGroup(world, name);
                suffix = security.getGroupSuffix(world, group);
                if (suffix == null) {
                    suffix = "";
                }
            }
            return suffix.replace(String.valueOf((char)194), "");
        } catch (Exception e) {
            System.out.println(e.getMessage());
            return "";
        }
    }

    public boolean isAdmin(Player p) {
        return security.has(p, "herochat.admin");
    }

    public boolean canCreate(Player p) {
        boolean admin = security.has(p, "herochat.admin");
        boolean create = security.has(p, "herochat.create");
        return admin || create;
    }

}
