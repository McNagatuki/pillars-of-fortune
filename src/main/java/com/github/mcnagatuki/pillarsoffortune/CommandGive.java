package com.github.mcnagatuki.pillarsoffortune;

import org.bukkit.Bukkit;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.*;
import java.util.stream.Collectors;

// /pof give ターゲットセレクター

public class CommandGive {
    private PillarsOfFortune plugin;
    CommandGive() {
        plugin = PillarsOfFortune.plugin;
    }

    public boolean run(CommandSender sender, String[] args) {
        if (args.length != 2){
            return false;
        }

        String playerSelector = args[1];
        List<Player> targets = Bukkit.selectEntities(sender, playerSelector).stream()
                .filter(entity -> entity instanceof Player)
                .map(entity -> (Player) entity)
                .collect(Collectors.toList());

        if (targets.isEmpty()) {
            sender.sendMessage("No players found with selector: " + playerSelector);
            return true;
        }

        List<ItemStack> chestsContents = plugin.chestManager.getChestsContents();
        if (chestsContents.isEmpty()){
            sender.sendMessage("No items found in chests.");
            return true;
        }

        for (Player target : targets) {
            Random random = new Random();
            int randomNumber = random.nextInt(chestsContents.size());

            ItemStack randomItem = chestsContents.get(randomNumber);
            target.getInventory().addItem(randomItem);
        }

        return true;
    }

    public List<String> getSuggestions(CommandSender sender, String[] args){
        if (args.length < 2){
            return new ArrayList<>();
        }

        if (args.length == 2){
            List<String> completions = new ArrayList<>();
            completions.addAll(Bukkit.getOnlinePlayers().stream()
                    .map(Player::getName)
                    .collect(Collectors.toList()));
            completions.add("@a");
            completions.add("@p");
            completions.add("@r");

            return completions.stream()
                    .filter(name -> name.startsWith(args[1]))
                    .collect(Collectors.toList());
        }

        return new ArrayList<>();
    }
}
