package com.github.mcnagatuki.pillarsoffortune;

//  /pof chest add x, y, z, name
//  /pof chest remove name
//  /pof chest clear
//  /pof chest list

import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandChest {
    private PillarsOfFortune plugin;

    CommandChest() {
        plugin = PillarsOfFortune.plugin;
    }

    public boolean run(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return false;
        }

        if (args[1].equals("add")) {
            return add(sender, args);
        }

        if (args[1].equals("remove")) {
            return remove(sender, args);
        }

        if (args[1].equals("clear")) {
            return clear(sender);
        }

        if (args[1].equals("list")) {
            return list(sender);
        }

        return true;
    }

    private boolean add(CommandSender sender, String[] args) {
        if (args.length != 6) {
            return false;
        }

        // CommandSender のタイプをチェックして、Location を取得
        Optional<Location> baseLocation = CommandUtils.getCommandSenderLocation(sender);
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

        // Locationにあるチェストの登録
        String chestName = args[5];
        ChestManager.OperationResult result = plugin.chestManager.addChest(chestName, location.get());
        if (result == ChestManager.OperationResult.SUCCESS) {
            sender.sendMessage("Chest " + chestName + " was added.");
            return true;
        } else if (result == ChestManager.OperationResult.INVALID_LOCATION) {
            sender.sendMessage("Specified location was invalid.");
            return false;
        } else {
            sender.sendMessage("Invalid input.");
            return false;
        }
    }

    private boolean remove(CommandSender sender, String[] args) {
        if (args.length != 3) {
            return false;
        }

        String chestName = args[2];
        ChestManager.OperationResult result = plugin.chestManager.removeChest(chestName);

        if (result == ChestManager.OperationResult.SUCCESS) {
            sender.sendMessage(chestName + " was removed.");
            return true;
        } else {
            sender.sendMessage("Invalid input.");
            return false;
        }
    }

    private boolean clear(CommandSender sender) {
        plugin.chestManager.clearChest();
        sender.sendMessage("All chests were removed.");
        return true;
    }

    private boolean list(CommandSender sender) {
        Map<String, ChestWrapper> chestMap = plugin.chestManager.getChestMap();

        ArrayList<String> messages = new ArrayList<>();
        messages.add("Chest locations...");
        chestMap.forEach((name, chestWrapper) -> {
            String message = "  " + name + ": " + chestWrapper.getLocation().toString();
            messages.add(message);
        });
        messages.forEach(sender::sendMessage);
        return true;
    }

    public List<String> getSuggestions(CommandSender sender, String[] args) {
        if (args.length < 2) {
            return new ArrayList<>();
        }

        if (args.length == 2) {
            return Stream.of("add", "remove", "clear", "list")
                    .filter(e -> e.startsWith(args[1]))
                    .collect(Collectors.toList());
        }

        // args.length >= 3
        if (args[1].equals("clear") || args[1].equals("list")) {
            return new ArrayList<>();
        }

        if (args[1].equals("remove")) {
            if (args.length == 3){
                return plugin.chestManager.getChestNames().stream()
                        .filter(e -> e.startsWith(args[2]))
                        .collect(Collectors.toList());
            }
            return new ArrayList<>();
        }

        if (args[1].equals("add")) {
            Optional<Location> baseLocation = CommandUtils.getCommandSenderLocation(sender);
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
            if (args.length == 6 && args[5].length() == 0) {
                return Collections.singletonList("chest-name");
            }

            return new ArrayList<>();
        }

        return new ArrayList<>();
    }
}
