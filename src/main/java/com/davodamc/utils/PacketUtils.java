package com.davodamc.utils;

import net.minecraft.server.v1_8_R3.*;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

import java.util.Random;

public class PacketUtils {
    private static final Random random = new Random();

    public static void sendPlatformPacket(Player player, Location location) {
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(
                world,
                new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ())
        );
        Material[] materials = {Material.GRASS, Material.STONE, Material.GLASS};
        packet.block = Block.getById(materials[random.nextInt(materials.length)].getId()).getBlockData();
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void removePlatformPacket(Player player, Location location) {
        WorldServer world = ((CraftWorld) location.getWorld()).getHandle();
        PacketPlayOutBlockChange packet = new PacketPlayOutBlockChange(
                world,
                new BlockPosition(location.getBlockX(), location.getBlockY(), location.getBlockZ())
        );
        packet.block = Block.getById(Material.AIR.getId()).getBlockData();
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }

    public static void sendActionBar(Player player, String message) {
        PacketPlayOutChat packet = new PacketPlayOutChat(new ChatComponentText(ChatAPI.cc(message)), (byte) 2);
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}
