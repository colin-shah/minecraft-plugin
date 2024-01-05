package com.minecraftdemo;

import org.bukkit.Location;
import org.bukkit.Material;

public class MachineState {
    private Location location;
    private boolean isActive;
    private Material baseMaterial; // To store the type of block used as base

    public MachineState(Location location, Material baseMaterial) {
        this.location = location;
        this.isActive = true; // Initially active when created
        this.baseMaterial = baseMaterial;
    }

    // Getters and setters
    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public boolean isActive() {
        return isActive;
    }

    public void setActive(boolean active) {
        isActive = active;
    }

    public Material getBaseMaterial() {
        return baseMaterial;
    }

    public void setBaseMaterial(Material baseMaterial) {
        this.baseMaterial = baseMaterial;
    }
}

