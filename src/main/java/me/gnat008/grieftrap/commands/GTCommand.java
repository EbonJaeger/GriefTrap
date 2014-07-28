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

package me.gnat008.grieftrap.commands;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import me.gnat008.grieftrap.GTMain;
import me.gnat008.grieftrap.regions.ProtectedRegion;
import me.gnat008.grieftrap.regions.RegionException;
import me.gnat008.grieftrap.regions.RegionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Gnat008
 */
public class GTCommand implements CommandExecutor {
    
    private GTMain plugin;
    
    private enum SubCommand {ADD, DEFINE, HELP, INFO, LIST, RELOAD, REMOVE, UNDEFINE}
    
    public Map<String, List<Location>> points = new HashMap<String, List<Location>>();
    
    public GTCommand(GTMain plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Check if the command is sent from console
        if (!(sender instanceof Player)) {
            if (args.length == 1 && args[0].equalsIgnoreCase("reload")) {
                reload(null);
            }
            
            return true;
        }
        
        Player player = (Player) sender;
        
        // Check if the command is the Help command
        if ((args.length == 0) || (args.length == 1 && args[0].equalsIgnoreCase("help"))) {
            showHelp(player);
            return true;
        }
        
        // See if the second arg is a valid subcommand
        SubCommand subCommand;
        try {
            subCommand = SubCommand.valueOf(args[0].toUpperCase());
        } catch (IllegalArgumentException ex) {
            plugin.getPrinter().printToPlayer(player, "Invalid subcommand! Type /gt help for help.", true);
            return true;
        }
        
        // Check if the player has permission to perform the command
        if (!args[0].equalsIgnoreCase("help") && !plugin.hasPermission(player, args[0])) {
            plugin.getPrinter().printToPlayer(player, "You do not have permission to do that!", true);
            return true;
        }
        
        // Execute the correct command
        switch(subCommand) {
            case HELP:
                showHelp(player);
                return true;
                
            case ADD:
                if (args.length != 3) {
                    plugin.getPrinter().printToPlayer(player, "Wrong number of arguments! See /gt help for help", true);
                    return true;
                } else {
                    String uuid = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                    try {
                        RegionManager.getRegionManager(plugin).addMember(args[1], uuid);
                        plugin.getPrinter().printToPlayer(player, "Member added to region!", false);
                    } catch (RegionException ex) {
                        plugin.getPrinter().printToPlayer(player, ex.getMessage(), true);
                    }
                    
                    return true;
                }
            
            case DEFINE:
                if (args.length != 1) {
                    plugin.getPrinter().printToPlayer(player, "Invalid arguments! Type /gt help for help.", true);
                    return true;
                } else {
                    plugin.playersPerformingCommand.put(player.getUniqueId().toString(), 1);
                    plugin.getPrinter().printToPlayer(player, "Click a block to select the first point.", false);
                    return true;
                }
            
            case INFO:
                if (args.length != 2) {
                    plugin.getPrinter().printToPlayer(player, "Invalid arguments! Type /gt help for help.", true);
                    return true;
                } else {
                    // TODO: Region info command
                }
            
            case LIST:
                if (args.length > 2 || args.length != 1) {
                    plugin.getPrinter().printToPlayer(player, "Invalid arguments! Type /gt help for help.", true);
                    return true;
                } else {
                    if (args.length == 2) {
                        int page;
                        try {
                            page = Integer.parseInt(args[1]);
                        } catch (NumberFormatException ex) {
                            plugin.getPrinter().printToPlayer(player, "Argument must be a number!", true);
                            return true;
                        }
                        
                        list(player, page);
                        return true;
                    } else {
                        list(player, 0);
                    }
                }
            
            case RELOAD:
                if (args.length != 1) {
                    plugin.getPrinter().printToPlayer(player, "Invalid arguments! Type /gt help for help.", true);
                    return true;
                } else {
                    reload(player);
                    return true;
                }
            
            case REMOVE:
                if (args.length != 3) {
                    plugin.getPrinter().printToPlayer(player, "Invalid arguments! Type /gt help for help.", true);
                    return true;
                } else {
                    String uuid = Bukkit.getOfflinePlayer(args[2]).getUniqueId().toString();
                    try {
                        RegionManager.getRegionManager(plugin).removeMember(uuid, args[1]);
                        plugin.getPrinter().printToPlayer(player, "Member removed from region!", false);
                    } catch (RegionException ex) {
                        plugin.getPrinter().printToPlayer(player, ex.getMessage(), true);
                        return true;
                    }
                }
            
            case UNDEFINE:
                if (args.length != 2) {
                    plugin.getPrinter().printToPlayer(player, "Invalid arguments! Type /gt help for help.", true);
                    return true;
                } else {
                    try {
                        RegionManager.getRegionManager(plugin).removeRegion(args[1]);
                        plugin.getPrinter().printToPlayer(player, "Region removed successfully!", false);
                        return true;
                    } catch (RegionException ex) {
                        plugin.getPrinter().printToPlayer(player, ex.getMessage(), true);
                        return true;
                    }
                }
        }
        
        return false;
    }
    
    private void showHelp(Player player) {
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "     GriefTrap Help Page:");
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "Version: " + ChatColor.AQUA + plugin.getDescription().getVersion());
        player.sendMessage(ChatColor.GREEN + "Author: " + ChatColor.AQUA + plugin.getDescription().getAuthors().toString());
        player.sendMessage("");
        player.sendMessage(ChatColor.GREEN + "/gt help" + ChatColor.AQUA + ": Shows this help page.");
        player.sendMessage(ChatColor.GREEN + "/gt add <region id> <player>" + ChatColor.AQUA + ": Adds a player to a specified region.");
        player.sendMessage(ChatColor.GREEN + "/gt define" + ChatColor.AQUA + ": Creates a region. Follow the chat prompts.");
        player.sendMessage(ChatColor.GREEN + "/gt info <region id>" + ChatColor.AQUA + ": Displays a region's info.");
        player.sendMessage(ChatColor.GREEN + "/gt list [page]" + ChatColor.AQUA + ": Shows a list of regions.");
        player.sendMessage(ChatColor.GREEN + "/gt reload" + ChatColor.AQUA + ": Reloads the plugin configuration.");
        player.sendMessage(ChatColor.GREEN + "/gt remove <region id> <player>" + ChatColor.AQUA + ": Removes a player from a region.");
        player.sendMessage(ChatColor.GREEN + "/gt undefine <region id>" + ChatColor.AQUA + ": Removes a specified region.");
    }
    
    private void list(Player player, int page) {
        if (page < 0) {
            page = 0;
        }
        
        List<String> regions = new ArrayList<String>();
        for (ProtectedRegion region : RegionManager.getRegionManager(plugin).getRegions().values()) {
            regions.add(region.getID());
        }
        Collections.sort(regions);
        
        final int totalSize = regions.size();
        final int pageSize = 10;
        final int pages = (int) Math.ceil(totalSize / (float) pageSize);
        
        player.sendMessage(ChatColor.GREEN + "Regions: (page " + (page + 1) + " of " + pages + ")");
        if (page < pages) {
            for (int i = page * pageSize; i < page * pageSize; i++) {
                if (i >= pageSize) {
                    break;
                }
                
                player.sendMessage(ChatColor.AQUA + regions.get(i));
            }
        }
    }
    
    private void reload(Player player) {
        // TODO: Reload main config
    }
}
