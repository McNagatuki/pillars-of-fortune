package com.github.mcnagatuki.pillarsoffortune;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.block.Chest;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;

public class ChestWrapper {
    protected Location location;

    ChestWrapper(Location location) {
        this.location = location;
    }

    public Location getLocation() {
        return this.location;
    }

    public boolean isValid(){
        Block block = this.location.getBlock();
        return block.getType() == Material.CHEST;
    }

    public Optional<ItemStack[]> getChestContents() {
        // 座標からブロックを取得
        Block block = this.location.getBlock();

        // チェストであるか確認
        if (block.getType() == Material.CHEST) {
            Chest chest = (Chest) block.getState();
            Inventory inventory = chest.getBlockInventory();
            ItemStack[] contents = inventory.getContents();
            return Optional.of(contents);
        } else {
            return Optional.empty();
        }
    }
}
