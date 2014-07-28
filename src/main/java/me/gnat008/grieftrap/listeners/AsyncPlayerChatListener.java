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

package me.gnat008.grieftrap.listeners;

import java.util.List;
import me.gnat008.grieftrap.GTMain;
import me.gnat008.grieftrap.regions.RegionException;
import me.gnat008.grieftrap.regions.RegionManager;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

/**
 *
 * @author Gnat008
 */
public class AsyncPlayerChatListener implements Listener {
    
    private GTMain plugin;
    
    public AsyncPlayerChatListener(GTMain plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.HIGHEST)
    public void onPlayerChat(AsyncPlayerChatEvent event) {
        Player player = event.getPlayer();
        
        if (plugin.playersPerformingCommand.containsKey(player.getUniqueId().toString()) && 
                plugin.playersPerformingCommand.get(player.getUniqueId().toString()) == 3) {
            String id = null;
            if (event.getMessage().contains(" ")) {
                plugin.getPrinter().printToPlayer(player, "Region ID's cannot contain spaces! Please try again.", true);
                event.setCancelled(true);
                return;
            } else {
                id = event.getMessage();
                event.setCancelled(true);
            }
            
            List<Location> points = PlayerInteractListener.points.get(player.getUniqueId().toString());
            if (id != null && points != null && !points.isEmpty()) {
                try {
                    RegionManager.getRegionManager(plugin).createRegion(id, player.getUniqueId().toString(), points.get(0), points.get(1));
                    plugin.getPrinter().printToPlayer(player, "Region created successfully!", false);
                } catch (RegionException ex) {
                    plugin.getPrinter().printToPlayer(player, ex.getMessage(), true);
                }
            }
            
            plugin.playersPerformingCommand.remove(player.getUniqueId().toString());
        }
    }
}
