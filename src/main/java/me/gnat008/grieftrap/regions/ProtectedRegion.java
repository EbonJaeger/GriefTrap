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
import java.util.List;
import org.bukkit.Location;

/**
 * A Class that represents a region. Will store things like coords, owner, etc.
 * 
 * @author Gnat008
 */
public class ProtectedRegion {
    
    private String id;
    private String owner;
    private List<String> members;
    private Location pt1;
    private Location pt2;
    
    public ProtectedRegion(String id, String owner, Location pt1, Location pt2) {
        this.id = id;
        this.owner = owner;
        this.members = new ArrayList<String>();
        this.pt1 = pt1;
        this.pt2 = pt2;
    }
    
    public void addMember(String member) {
        this.members.add(member);
    }
    
    public void removeMember(String member) {
        this.members.remove(member);
    }
    
    public void setMembers(List<String> members) {
        this.members = members;
    }
    
    public String getID() {
        return this.id;
    }
    
    public String getOwner() {
        return this.owner;
    }
    
    public List<String> getMembers() {
        return this.members;
    }
    
    public List<Location> getPoints() {
        List<Location> points = new ArrayList<Location>();
        points.add(pt1);
        points.add(pt2);
        
        return points;
    }
}
