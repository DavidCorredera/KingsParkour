package com.davodamc.parkour;

import com.davodamc.Main;
import com.davodamc.utils.ChatAPI;
import com.davodamc.utils.ParticleUtils;
import com.davodamc.utils.PacketUtils;
import net.minecraft.server.v1_8_R3.EnumParticle;
import org.bukkit.*;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;

import javax.annotation.Nullable;
import java.util.*;

public class ParkourModule {

    private final Random random = new Random();
    private final Map<Player, ParkourData> playerParkourData = new HashMap<>();

    public void generateInitialPlatform(Player player) {
        Location startLocation = player.getLocation().add(0, 20, 0);

        if (!isAreaClear(startLocation)) {
            player.sendMessage(ChatAPI.cc(ChatAPI.prefix + "&c¡No hay suficiente espacio para generar el parkour!"));
            return;
        }

        // Inicializar el ParkourData para el jugador
        ParkourData data = new ParkourData();
        data.setTime(0);
        startTimer(data);
        player.teleport(startLocation.getBlock().getLocation().clone().add(0.5, 1.2, 0.5));

        List<Location> platforms = new ArrayList<>();
        platforms.add(startLocation);
        data.setPlatforms(platforms);

        PacketUtils.sendPlatformPacket(player, startLocation);

        player.sendMessage("");
        player.sendMessage(ChatAPI.cc("&9&lKINGSCRAFT &8- &bParkour"));
        player.sendMessage("");
        player.sendMessage(ChatAPI.cc("&f¡Bienvenido &b" + player.getName() + " &fal parkour de KingsCraft!"));
        player.sendMessage(ChatAPI.cc("&fSalta de plataforma en plataforma sin caerte. ¡Suerte!"));
        player.sendMessage("");

        BukkitRunnable parkourTask = createParkourTask(player, data);
        data.setParkourTask(parkourTask);
        parkourTask.runTaskTimer(Main.getInstance(), 0L, 5L);

        // Almacenar los datos del jugador
        playerParkourData.put(player, data);
    }

    private void startTimer(ParkourData data) {
        BukkitRunnable timer = new BukkitRunnable() {
            @Override
            public void run() {
                data.setTime(data.getTime() + 1);
            }
        };
        timer.runTaskTimerAsynchronously(Main.getInstance(), 0L, 20L);
        data.setTimer(timer);
    }

    private BukkitRunnable createParkourTask(Player player, ParkourData data) {
        return new BukkitRunnable() {
            @Override
            public void run() {
                List<Location> platforms = data.getPlatforms();
                if (platforms == null || platforms.isEmpty()) return;

                Location currentPlatform = platforms.get(platforms.size() - 1);

                if (isOnPlatform(player, currentPlatform)) {
                    if (player.getLocation().getY() > currentPlatform.getY()) {
                        removePreviousPlatform(player, data);
                        generateNextPlatform(player, data);
                        PacketUtils.sendActionBar(player, "&f¡Buen salto! &b+1");
                        player.playSound(player.getLocation(), Sound.NOTE_PIANO, 1.0f, 1.0f);
                        data.setPoints(data.getPoints() + 1);
                    }
                } else if (player.getLocation().getY() < currentPlatform.getY() - 1.5) {
                    resetParkour(player);
                }
            }
        };
    }

    private void removePreviousPlatform(Player player, ParkourData data) {
        List<Location> platforms = data.getPlatforms();
        if (platforms != null && platforms.size() > 1) {
            Location previousPlatform = platforms.get(platforms.size() - 2);
            ParticleUtils.showParticles(player, previousPlatform, EnumParticle.SMOKE_NORMAL, 8);
            PacketUtils.removePlatformPacket(player, previousPlatform);
        }
    }

    private void resetParkour(Player player) {
        cancelTimer(player);

        ParkourData data = playerParkourData.get(player);
        if (data != null) {
            List<Location> platforms = data.getPlatforms();
            if (platforms != null) {
                platforms.forEach(loc -> PacketUtils.removePlatformPacket(player, loc));
            }

            player.teleport(new Location(player.getWorld(), -4.5, 84.3, 267.5, 90, 0));
            player.sendMessage(ChatAPI.cc(ChatAPI.prefix + "&f¡Te has caído del &bparkour&f!"));
            sendStats(player);

            playerParkourData.remove(player);
            BukkitRunnable task = data.getParkourTask();
            if (task != null) task.cancel();
        }
    }

    private void cancelTimer(Player player) {
        ParkourData data = playerParkourData.get(player);
        if (data != null) {
            BukkitRunnable timer = data.getTimer();
            if (timer != null) timer.cancel();
        }
    }

    private boolean isOnPlatform(Player player, Location platformLocation) {
        double tolerance = 0.75;
        Location playerLocation = player.getLocation();

        boolean isWithinX = Math.abs(playerLocation.getX() - platformLocation.getX()) <= tolerance;
        boolean isWithinZ = Math.abs(playerLocation.getZ() - platformLocation.getZ()) <= tolerance;
        boolean isAbovePlatform = playerLocation.getY() >= platformLocation.getY()
                && playerLocation.getY() <= platformLocation.getY() + 1;

        return isWithinX && isWithinZ && isAbovePlatform;
    }

    public ParkourData getPlayerParkourData(Player player) {
        return playerParkourData.get(player); // Obtiene los datos del jugador si existen
    }

    public void generateNextPlatform(Player player, @Nullable ParkourData data) {
        if (data == null) return;
        List<Location> platforms = data.getPlatforms();
        if (platforms == null) return;

        Location currentPlatform = platforms.get(platforms.size() - 1);
        Location nextPlatform;

        do {
            nextPlatform = currentPlatform.clone().add(
                    random.nextInt(6) - 3, random.nextInt(2), random.nextInt(6) - 3
            );
        } while (!isAreaClear(nextPlatform) || !isMinDistanceValid(currentPlatform, nextPlatform));

        ParticleUtils.showParticles(player, nextPlatform, EnumParticle.FIREWORKS_SPARK, 6);
        platforms.add(nextPlatform);
        PacketUtils.sendPlatformPacket(player, nextPlatform);
    }

    private boolean isMinDistanceValid(Location current, Location next) {
        double minHorizontalDistance = 1.5;
        double maxVerticalDistance = 1;

        double deltaX = Math.abs(current.getX() - next.getX());
        double deltaZ = Math.abs(current.getZ() - next.getZ());
        double deltaY = Math.abs(current.getY() - next.getY());

        return deltaX >= minHorizontalDistance || deltaZ >= minHorizontalDistance && deltaY <= maxVerticalDistance;
    }

    private boolean isAreaClear(Location location) {
        for (int x = -1; x <= 1; x++) {
            for (int z = -1; z <= 1; z++) {
                for (int y = 0; y < 2; y++) {
                    if (location.clone().add(x, y, z).getBlock().getType() != Material.AIR) return false;
                }
            }
        }
        return true;
    }

    private void sendStats(Player player) {
        ParkourData data = playerParkourData.get(player);
        if (data == null) return;

        int points = data.getPoints();
        int time = data.getTime();
        double timeBetweenJumps = 0;
        double jumpsPerSecond = 0;

        if (points > 0 && time > 0) {
            timeBetweenJumps = Math.floor(((double) time / points) * 100) / 100.0;
            jumpsPerSecond = Math.floor(((double) points / time) * 100) / 100.0;
        }

        player.sendMessage("");
        player.sendMessage(ChatAPI.cc("&9&lKINGSCRAFT &8- &bParkour"));
        player.sendMessage("");
        player.sendMessage(ChatAPI.cc("&fPuntos: &b" + points));
        player.sendMessage(ChatAPI.cc("&fTiempo: &b" + time + " segundos"));
        player.sendMessage(ChatAPI.cc("&fSaltos por segundo: &b" + jumpsPerSecond));
        player.sendMessage(ChatAPI.cc("&fTiempo entre saltos: &b" + timeBetweenJumps + " segundos"));
        player.sendMessage("");
    }
}