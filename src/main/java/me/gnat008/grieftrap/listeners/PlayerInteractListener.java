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

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.gnat008.grieftrap.GTMain;
import org.bukkit.Location;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;

/**
 *
 * @author Gnat008
 */
public class PlayerInteractListener implements Listener {
    
    private GTMain plugin;
    
    public static Map<String, List<Location>> points = new HashMap<String, List<Location>>();
    
    public PlayerInteractListener(GTMain plugin) {
        this.plugin = plugin;
    }
    
    @EventHandler(priority = EventPriority.MONITOR)
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        Location pt1 = null;
        Location pt2 = null;
        if (plugin.playersPerformingCommand.containsKey(player.getUniqueId().toString()) && 
                plugin.playersPerformingCommand.get(player.getUniqueId().toString()) == 1) {
            pt1 = event.getClickedBlock().getLocation();
            plugin.playersPerformingCommand.put(player.getUniqueId().toString(), 2);
            
            plugin.getPrinter().printToPlayer(player, "Click another block to select the second point.", false);
        } else if (plugin.playersPerformingCommand.containsKey(player.getUniqueId().toString()) && 
                plugin.playersPerformingCommand.get(player.getUniqueId().toString()) == 2) {
            pt2 = event.getClickedBlock().getLocation();
            plugin.playersPerformingCommand.put(player.getUniqueId().toString(), 3);
        }
        
        if (plugin.playersPerformingCommand.containsKey(player.getUniqueId().toString()) && 
                plugin.playersPerformingCommand.get(player.getUniqueId().toString()) == 3 && 
                pt1 != null && pt2 != null) {
            List<Location> pts = new ArrayList<Location>();
            pts.add(pt1);
            pts.add(pt2);
            points.put(player.getUniqueId().toString(), pts);
            
            plugin.getPrinter().printToPlayer(player, "Type a name in chat to set the region ID.", false);
        }
    }
}
