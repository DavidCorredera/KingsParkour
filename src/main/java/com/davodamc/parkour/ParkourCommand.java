package com.davodamc.parkour;

import com.davodamc.Main;
import com.davodamc.utils.ChatAPI;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.scheduler.BukkitRunnable;
import org.bukkit.scheduler.BukkitTask;

import static com.davodamc.parkour.ParkourModule.parkourTasks;

public class ParkourCommand implements CommandExecutor {

    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por jugadores.");
            return true;
        }
        Player p = (Player) sender;
        BukkitRunnable parkourTask = parkourTasks.get(p);

        if (parkourTask != null) {
            p.sendMessage(ChatAPI.cc(ChatAPI.prefix + "&c¡Ya estás en el parkour!"));
            return true;
        }
            Main.getInstance().getParkourManager().generateInitialPlatform(p);
            Main.getInstance().getParkourManager().generateNextPlatform(p);
        return true;
    }
}