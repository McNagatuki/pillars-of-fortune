package com.github.mcnagatuki.pillarsoffortune;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class CommandManager implements CommandExecutor, TabCompleter {
    private final String prefixAccept = ChatColor.GREEN + "[PillarsOfFortune]" + ChatColor.RESET + " ";
    private final String prefixReject = ChatColor.RED + "[PillarsOfFortune]" + ChatColor.RESET + " ";
    private final String[] HELP_MESSAGE = {
            "[ " + ChatColor.GREEN + "Pillars of Fortune" + ChatColor.RESET + " ] -----------------",
            "/pof help : ヘルプ表示",
            "/pof chest add x, y, z, name",
            "/pof chest remove name",
            "/pof chest clear",
            "/pof chest list",
            "/pof give ターゲットセレクター",
            "-----------------------------------------------------",
    };

    private CommandChest commandChest;
    private CommandGive commandGive;

    CommandManager(){
        commandChest = new CommandChest();
        commandGive = new CommandGive();
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (args.length < 1) {
            return false;
        }

        // help
        if (args[0].equals("help")) {
            Stream.of(HELP_MESSAGE).forEach(sender::sendMessage);
            return true;
        }

        // chest
        if (args[0].equals("chest")) {
            return commandChest.run(sender, args);
        }

        // give
        if (args[0].equals("give")) {
            return commandGive.run(sender, args);
        }

        Stream.of(HELP_MESSAGE).forEach(sender::sendMessage);
        return false;
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String alias, String[] args) {
        if (args.length == 1) {
            return Stream.of("help", "chest", "give")
                    .filter(e -> e.startsWith(args[0]))
                    .collect(Collectors.toList());
        }

        // chest
        if (args.length > 1 && args[0].equals("chest")) {
            return commandChest.getSuggestions(sender, args);
        }

        // give
        if (args.length > 1 && args[0].equals("give")) {
            return commandGive.getSuggestions(sender, args);
        }

        return new ArrayList<>();
    }

}