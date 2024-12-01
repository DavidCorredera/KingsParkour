package com.davodamc.utils;

import org.bukkit.ChatColor;

import java.util.List;
import java.util.stream.Collectors;

public abstract class ChatAPI {

    public static String prefix = cc("&9&lKingsCraft &8» &f");

    public static String cc(String msg) {
        return ChatColor.translateAlternateColorCodes('&', msg);
    }

    public static List<String> ccList(List<String> list) {return list.stream().map(ChatAPI::cc).collect(Collectors.toList());}
}