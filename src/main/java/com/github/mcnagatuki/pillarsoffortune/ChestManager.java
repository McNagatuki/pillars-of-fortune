package com.github.mcnagatuki.pillarsoffortune;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

public class ChestManager {
    private Map<String, ChestWrapper> chestMap;

    public enum OperationResult{
        SUCCESS,
        INVALID_LOCATION,
        INVALID_NAME;
    }

    ChestManager() {
        chestMap = new HashMap<String, ChestWrapper>();
    }

    public OperationResult addChest(String name, Location location){
        ChestWrapper newChest = new ChestWrapper(location);
        if (!newChest.isValid()){
            return OperationResult.INVALID_LOCATION;
        }

        if (chestMap.containsKey(name)){
            return OperationResult.INVALID_NAME;
        }

        chestMap.put(name, newChest);
        return OperationResult.SUCCESS;
    }

    public OperationResult removeChest(String name){
        if (!chestMap.containsKey(name)){
            return OperationResult.INVALID_NAME;
        }

        chestMap.remove(name);
        return OperationResult.SUCCESS;
    }

    public void clearChest(){
        chestMap.clear();
    }

    public Map<String, ChestWrapper> getChestMap() {
        return chestMap;
    }

    public List<String> getChestNames(){
        return chestMap.keySet().stream().collect(Collectors.toList());
    }

    public List<ItemStack> getChestsContents() {
        List<ItemStack> itemStacks = chestMap.values().stream()
                .map(e -> e.getChestContents())
                .filter(e -> e.isPresent())
                .map(e -> e.get())
                .flatMap(Arrays::stream)
                .filter(e -> e != null)
                .collect(Collectors.toList());
        return itemStacks;
    }
}
