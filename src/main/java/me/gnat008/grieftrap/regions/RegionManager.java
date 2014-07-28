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

package me.gnat008.grieftrap.regions;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import me.gnat008.grieftrap.GTMain;
import me.gnat008.grieftrap.configs.ConfigManager;
import org.bukkit.Bukkit;
import org.bukkit.Location;

/**
 * This Class manages the regions on a server.
 * 
 * @author Gnat008
 */
public class RegionManager {
    
    private int index = 0;
    private HashMap<String, ProtectedRegion> regions = new HashMap<String, ProtectedRegion>();
    private HashMap<String, Integer> regionIndex = new HashMap<String, Integer>();
    
    private static RegionManager regionManager;
    
    private GTMain plugin;
    private ConfigManager regionConfig;
    
    private RegionManager(GTMain plugin) {
        this.plugin = plugin;
        this.regionConfig = plugin.getRegionConfig();
    }
    
    public static RegionManager getRegionManager(GTMain plugin) {
        if (regionManager == null) {
            regionManager = new RegionManager(plugin);
        }
        
        return regionManager;
    }
    
    /**
     * Creates a region with a given name, owner UUID, and two points.
     * 
     * @param id The name of the region.
     * @param ownerUUID The UUID of the creator of the region.
     * @param pt1 The first selected point of the region.
     * @param pt2 The second selected point of the region.
     * @return The created region or null.
     * @throws RegionException If a region with the given ID already exists.
     */
    public ProtectedRegion createRegion(String id, String ownerUUID, Location pt1, Location pt2) throws RegionException {
        if (!(regions.containsKey(id))) {
            ProtectedRegion region = new ProtectedRegion(id, ownerUUID, pt1, pt2);
            regions.put(id, region);
            regionIndex.put(id, index);
            
            regionConfig.getConfig().set("Regions." + index + ".id", id);
            regionConfig.getConfig().set("Regions." + index + ".owner", ownerUUID);
            regionConfig.getConfig().set("Regions." + index + ".pt1", serializeLocation(pt1));
            regionConfig.getConfig().set("Regions." + index + ".pt2", serializeLocation(pt2));
            regionConfig.getConfig().set("Regions." + index + ".members", new ArrayList<String>());
            
            List<Integer> indexList = regionConfig.getConfig().getIntegerList("Regions.Index");
            indexList.add(index);
            regionConfig.getConfig().set("Regions.Index", indexList);
            regionConfig.saveConfig();
            index++;
            
            return region;
        } else {
            throw new RegionException("A region with that name already exists!");
        }
    }
    
    public HashMap<String, ProtectedRegion> getRegions() {
        return this.regions;
    }
    
    /**
     * Adds a member to the region so they can build in it.
     * 
     * @param id The region's ID.
     * @param memberUUID The UUID of the added member.
     * @throws RegionException If no region with the given ID can be found.
     */
    public void addMember(String id, String memberUUID) throws RegionException {
        ProtectedRegion region;
        try {
            region = regions.get(id);
        } catch (NullPointerException ex) {
            throw new RegionException("No region with id '" + id + "' found!");
        }
        region.addMember(memberUUID);
        
        regionConfig.getConfig().set("Regions." + regionIndex.get(id) + ".members", region.getMembers());
    }
    
    /**
     * Removes a member from a region so that they can no longer build in it.
     * 
     * @param id The region's ID.
     * @param memberUUID The UUID of the removed member.
     * @throws RegionException If no region with the given ID can be found.
     */
    public void removeMember(String id, String memberUUID) throws RegionException {
        ProtectedRegion region;
        try {
            region = regions.get(id);
        } catch (NullPointerException ex) {
            throw new RegionException("No region with id '" + id + "' found!");
        }
        region.removeMember(memberUUID);
        
        regionConfig.getConfig().set("Regions." + regionIndex.get(id) + ".members", region.getMembers());
    }
    
    /**
     * Loads a region from the config file into memory.
     * 
     * @param index The index of the region in the config file.
     * @throws RegionException If a region with the same ID already exists, or if data is missing from the file.
     */
    public void loadRegion(int index) throws RegionException {
        String id = null;
        String owner = null;
        Location pt1 = null;
        Location pt2 = null;
        List<String> members = null;
        
        try {
            id = regionConfig.getConfig().getString("Regions." + index + ".id");
            owner = regionConfig.getConfig().getString("Regions." + index + ".owner");
            pt1 = deserializeLocation(regionConfig.getConfig().getString("Regions." + index + ".pt1"));
            pt2 = deserializeLocation(regionConfig.getConfig().getString("Regions." + index + ".pt2"));
            members = regionConfig.getConfig().getStringList("Regions." + index + ".members");
        } catch (NullPointerException ex) {
            throw new RegionException("Could not load region at file index '" + index + "'! Missing info.");
        }
        
        if (id != null && owner != null && pt1 != null && pt2 != null) {
            if (members == null || members.isEmpty()) {
                members = new ArrayList<String>();
                
                ProtectedRegion region = createRegion(id, owner, pt1, pt2);
                region.setMembers(members);
            }
        }
    }
    
    /**
     * Removes the region with the given ID from memory and the config.
     * 
     * @param id The region's ID.
     * @throws RegionException If no region with the given ID can be found.
     */
    public void removeRegion(String id) throws RegionException {
        int localIndex;
        try {
            localIndex = regionIndex.get(id);
        } catch (NullPointerException ex) {
            throw new RegionException("No region with id '" + id + "' found!");
        }
        regionConfig.getConfig().set("Regions." + localIndex, null);
        
        List<Integer> indexList = regionConfig.getConfig().getIntegerList("Regions.Index");
        indexList.remove(localIndex);
        regionConfig.getConfig().set("Regions.Index", localIndex);
        regionConfig.saveConfig();
        
        regions.remove(id);
        regionIndex.remove(id);
    }
    
    /**
     * Loads the regions from file into memory.
     * 
     * @throws RegionException If a region with the same ID already exists.
     */
    public void load() throws RegionException {
        index = 0;
        
        if (regionConfig.getConfig().getIntegerList("Regions.Index").isEmpty()) {
            return;
        }
        
        for (int i : regionConfig.getConfig().getIntegerList("Regions.Index")) {
            loadRegion(i);
        }
    }
    
    /**
     * Unloads all regions from memory and saves the config file. Called in onDisable().
     */
    public void unload() {
        plugin.getRegionConfig().saveConfig();
        regions.clear();
        regionIndex.clear();
    }
    
    /**
     * Serializes a given location into a single String.
     * 
     * @param loc The location to serialize.
     * @return The serialized location as a String.
     */
    public String serializeLocation(Location loc) {
        return loc.getWorld().getName() + "," + loc.getX() + "," + loc.getY() + "," + loc.getZ();
    }
    
    /**
     * De-serializes a location from a String.
     * 
     * @param s The String to de-serialize.
     * @return The location.
     */
    public Location deserializeLocation(String s) {
        String[] str = s.split(",");
        return new Location(Bukkit.getWorld(str[0]), Double.parseDouble(str[1]), Double.parseDouble(str[2]), Double.parseDouble(str[3]));
    }
}
