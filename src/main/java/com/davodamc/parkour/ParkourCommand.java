package com.davodamc.parkour;

import com.davodamc.Main;
import com.davodamc.utils.ChatAPI;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class ParkourCommand implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, org.bukkit.command.Command command, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Este comando solo puede ser ejecutado por jugadores.");
            return true;
        }

        Player p = (Player) sender;

        // Verificar si el jugador ya está en el parkour
        ParkourData parkourData = Main.getInstance().getParkourManager().getPlayerParkourData(p);

        if (parkourData != null) {
            p.sendMessage(ChatAPI.cc(ChatAPI.prefix + "&c¡Ya estás en el parkour!"));
            return true;
        }

        // Iniciar el parkour para el jugador
        Main.getInstance().getParkourManager().generateInitialPlatform(p);
        Main.getInstance().getParkourManager().generateNextPlatform(p, null);

        return true;
    }
}
