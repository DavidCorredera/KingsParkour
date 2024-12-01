package com.davodamc;

import com.davodamc.parkour.ParkourCommand;
import com.davodamc.parkour.ParkourModule;
import org.bukkit.plugin.java.JavaPlugin;

public class Main extends JavaPlugin {
    private static Main instance;
    private ParkourModule parkourModule;

    @Override
    public void onEnable() {
        // INSTANCIAS
        instance = this;
        this.parkourModule = new ParkourModule();

        // COMANDOS
        getCommand("parkour").setExecutor(new ParkourCommand());

        getLogger().info("¡El módulo de Parkour ha cargado correctamente!");
    }

    @Override
    public void onDisable() {
        getLogger().info("¡El módulo de Parkour se ha deshabilitado!");
    }

    public static Main getInstance() {
        return instance;
    }
    public ParkourModule getParkourManager() {return parkourModule;}
}