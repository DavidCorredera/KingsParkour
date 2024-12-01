package com.davodamc.utils;

import net.minecraft.server.v1_8_R3.EnumParticle;
import net.minecraft.server.v1_8_R3.PacketPlayOutWorldParticles;
import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.entity.CraftPlayer;
import org.bukkit.entity.Player;

public class ParticleUtils {

    public static void showParticles(Player player, Location location, EnumParticle particle, int count) {
        PacketPlayOutWorldParticles packet = new PacketPlayOutWorldParticles(
                particle, true,
                (float) location.getX(), (float) location.getY(), (float) location.getZ(),
                0.2f, 0.5f, 0.2f,
                0.1f, count
        );
        ((CraftPlayer) player).getHandle().playerConnection.sendPacket(packet);
    }
}