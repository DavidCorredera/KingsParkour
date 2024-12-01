package com.davodamc.parkour;

import org.bukkit.Location;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class ParkourData {
    private List<Location> platforms = new ArrayList<>();
    private int points = 0;
    private int time = 0;
    private BukkitRunnable parkourTask;
    private BukkitRunnable timer;

    // Getters y setters
    public List<Location> getPlatforms() {
        return platforms;
    }

    public void setPlatforms(List<Location> platforms) {
        this.platforms = platforms;
    }

    public int getPoints() {
        return points;
    }

    public void setPoints(int points) {
        this.points = points;
    }

    public int getTime() {
        return time;
    }

    public void setTime(int time) {
        this.time = time;
    }

    public BukkitRunnable getParkourTask() {
        return parkourTask;
    }

    public void setParkourTask(BukkitRunnable parkourTask) {
        this.parkourTask = parkourTask;
    }

    public BukkitRunnable getTimer() {
        return timer;
    }

    public void setTimer(BukkitRunnable timer) {
        this.timer = timer;
    }
}

