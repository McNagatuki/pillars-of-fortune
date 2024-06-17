package com.github.mcnagatuki.pillarsoffortune;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.CommandSender;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static com.github.mcnagatuki.pillarsoffortune.CommandUtils.getCommandSenderLocation;
import static com.github.mcnagatuki.pillarsoffortune.CommandUtils.locationToString;

public class CommandLava {
    private PillarsOfFortune plugin;
    int currentY;
    Location pos1;  // null注意
    Location pos2;  // null注意
    Material wallMaterial;  // null注意


    CommandLava() {
        plugin = PillarsOfFortune.plugin;
        currentY = Integer.MIN_VALUE;
        pos1 = null;
        pos2 = null;
        wallMaterial = null;
    }

    public boolean run(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        if (args[1].startsWith("pos")) {
            return pos(sender, args);
        }

        if (args[1].equals("wall")) {
            return wall(sender, args);
        }

        if (args[1].equals("set")) {
            return set(sender);
        }

        if (args[1].equals("initialize")){
            return initialize(sender);
        }

        return false;
    }

    private boolean pos(CommandSender sender, String[] args) {
        if (args.length != 5) {
            sender.sendMessage("Command usage: /pof lava pos1(2) x y z");
            return false;
        }

        // CommandSender のタイプをチェックして、Location を取得
        Optional<Location> baseLocation = getCommandSenderLocation(sender);
        if (!baseLocation.isPresent()){
            sender.sendMessage("Command must be run by a player or a command block.");
            return false;
        }

        // 座標をパースしLocationにする
        Optional<Location> location = CommandUtils.parseLocation(baseLocation.get(), args[2], args[3], args[4]);
        if (!location.isPresent()) {
            sender.sendMessage("Bad location.");
            return false;
        }

        if (args[1].equals("pos1")){
            pos1 = location.get();
            sender.sendMessage("Pos 1 was set to " + locationToString(location.get()));
            return true;
        }

        if (args[1].equals("pos2")){
            pos2 = location.get();
            sender.sendMessage("Pos 2 was set to " + locationToString(location.get()));
            return true;
        }

        return false;
    }

    private  boolean wall(CommandSender sender, String[] args) {
        if (args.length != 3) {
            return false;
        }

        // ブロックタイプの確認
        String blockType = args[2];
        Material material;
        try {
            material = Material.valueOf(blockType.toUpperCase());
        } catch (IllegalArgumentException e) {
            sender.sendMessage("Invalid block type: " + blockType);
            return false;
        }
        if (!material.isBlock()) {
            sender.sendMessage("The specified material is not a block: " + blockType);
            return false;
        }

        wallMaterial = material;
        sender.sendMessage("Wall material was set to " + blockType);
        return true;
    }

    private boolean set(CommandSender sender){
        // check base location
        Optional<Location> baseLocation = CommandUtils.getCommandSenderLocation(sender);
        if (!baseLocation.isPresent()) {
            return false;
        }
        World world = baseLocation.get().getWorld();

        // check position
        if (pos1 == null) {
            sender.sendMessage("You must set pos1.");
            return false;
        }
        if (pos2 == null) {
            sender.sendMessage("You must set pos2.");
            return false;
        }

        // check material
        if (wallMaterial == null) {
            sender.sendMessage("You must set wall material.");
            return false;
        }

        int sX = pos1.getBlockX();
        int sY = pos1.getBlockY();
        int sZ = pos1.getBlockZ();
        int eX = pos2.getBlockX();
        int eY = pos2.getBlockY();
        int eZ = pos2.getBlockZ();
        if (sX > eX){
            int temp = sX;
            sX = eX;
            eX = temp;
        }
        if (sY > eY){
            int temp = sY;
            sY = eY;
            eY = temp;
        }
        if (sZ > eZ){
            int temp = sZ;
            sZ = eZ;
            eZ = temp;
        }

        // check currentY
        if (currentY < sY) {
            currentY = sY;
        } else {
          currentY += 1;
        }

        if (currentY > eY) {
            sender.sendMessage("Lava rising was end.");
            return true;
        }

        for (int x = sX; x <= eX; x += 1){
            for (int z = sZ; z <= eZ; z += 1){
                double xDouble = (double) x;
                double yDouble = (double) currentY;
                double zDouble = (double) z;
                Location location = new Location(world,xDouble + 0.5, yDouble,zDouble+ 0.5);
                Block block = location.getBlock();

                if (x == sX || x == eX || currentY == sY || z == sZ || z == eZ) {
                    block.setType(wallMaterial);
                }
                else {
                    block.setType(Material.valueOf("LAVA"));
                }
            }
        }

        return true;
    }

    private boolean initialize(CommandSender sender) {
        currentY = Integer.MIN_VALUE;
        sender.sendMessage("Initialized.");
        return true;
    }

    public List<String> getSuggestions(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return new ArrayList<>();
        }

        if (args.length == 2) {
            return Stream.of("pos1", "pos2", "wall", "set", "initialize")
                    .filter(e -> e.startsWith(args[1]))
                    .collect(Collectors.toList());
        }

        // args.length >= 3
        if (args[1].equals("set")) {
            return new ArrayList<>();
        }

        if (args[1].equals("pos1") || args[1].equals("pos2")) {
            Optional<Location> baseLocation = getCommandSenderLocation(sender);
            if (!baseLocation.isPresent()){
                return new ArrayList<>();
            }

            if (args.length == 3 && args[2].length() == 0) {
                int posX = baseLocation.get().getBlockX();
                return Collections.singletonList(Integer.toString(posX));
            }
            if (args.length == 4 && args[3].length() == 0) {
                int posY = baseLocation.get().getBlockY();
                return Collections.singletonList(Integer.toString(posY));
            }
            if (args.length == 5 && args[4].length() == 0) {
                int posZ = baseLocation.get().getBlockZ();
                return Collections.singletonList(Integer.toString(posZ));
            }

            return new ArrayList<>();
        }

        if (args[1].equals("wall")) {
            if (args.length == 3){
                return Arrays.stream(Material.values())
                        .filter(e -> e.isBlock())
                        .map(e -> e.toString().toLowerCase())
                        .filter(e -> e.startsWith(args[2]))
                        .collect(Collectors.toList());
            }
            return new ArrayList<>();
        }

        return new ArrayList<>();
    }
}
