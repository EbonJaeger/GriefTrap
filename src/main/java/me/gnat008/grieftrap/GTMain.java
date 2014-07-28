/*
 * Copyright (C) 2014 Gnat008
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package me.gnat008.grieftrap;

import java.util.HashMap;
import me.gnat008.grieftrap.Util.Printer;
import me.gnat008.grieftrap.commands.GTCommand;
import me.gnat008.grieftrap.configs.ConfigManager;
import me.gnat008.grieftrap.listeners.AsyncPlayerChatListener;
import me.gnat008.grieftrap.listeners.PlayerInteractListener;
import me.gnat008.grieftrap.regions.RegionException;
import me.gnat008.grieftrap.regions.RegionManager;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

/**
 *
 * @author Gnat008
 */
public class GTMain extends JavaPlugin {
    
    private Printer printer;
    
    private ConfigManager mainConfig;
    private ConfigManager regionConfig;
    
    public HashMap<String, Integer> playersPerformingCommand;
    
    @Override
    public void onEnable() {
        this.printer = new Printer(this);
        this.playersPerformingCommand = new HashMap<String, Integer>();
        
        // Set the CommandExecutor
        getCommand("grieftrap").setExecutor(new GTCommand(this));
        
        // Register Listeners
        getServer().getPluginManager().registerEvents(new AsyncPlayerChatListener(this), this);
        getServer().getPluginManager().registerEvents(new PlayerInteractListener(this), this);
        
        // Load/create config files
        // TODO: Plugin configuration file
        this.regionConfig = new ConfigManager(this, "regions.yml");
        regionConfig.saveConfig();
        
        // Load regions from file
        try {
            RegionManager.getRegionManager(this).load();
        } catch (RegionException ex) {
            printer.printToConsole(ex.getMessage(), true);
        }
    }
    
    @Override
    public void onDisable() {
        RegionManager.getRegionManager(this).unload();
        // TODO: Save configs
    }
    
    public ConfigManager getMainConfig() {
        return mainConfig;
    }
    
    public ConfigManager getRegionConfig() {
        return regionConfig;
    }
    
    public Printer getPrinter() {
        return this.printer;
    }
    
    public boolean hasPermission(Player player, String node) {
        return player.hasPermission("grieftrap." + node.toLowerCase());
    }
}
