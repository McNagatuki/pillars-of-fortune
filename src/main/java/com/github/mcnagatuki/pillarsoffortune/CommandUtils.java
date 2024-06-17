package com.github.mcnagatuki.pillarsoffortune;

import org.bukkit.Location;
import org.bukkit.command.BlockCommandSender;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Optional;

public class CommandUtils {
    public static Optional<Location> getCommandSenderLocation(CommandSender sender) {
        Location baseLocation = null;
        if (sender instanceof Player) {
            baseLocation = ((Player) sender).getLocation();
            return Optional.of(baseLocation);
        }

        if (sender instanceof BlockCommandSender) {
            baseLocation = ((BlockCommandSender) sender).getBlock().getLocation();
            return Optional.of(baseLocation);
        }

        return Optional.empty();
    }

    public static Optional<Location> parseLocation(Location baseLocation, String xStr, String yStr, String zStr) {
        try {
            double x = parseCoordinate(baseLocation.getX(), xStr, true);
            double y = parseCoordinate(baseLocation.getY(), yStr, false);
            double z = parseCoordinate(baseLocation.getZ(), zStr, true);

            return Optional.of(new Location(baseLocation.getWorld(), x, y, z));
        } catch (NumberFormatException e) {
            return Optional.empty();
        }
    }

    public static double parseCoordinate(double baseValue, String coordStr, boolean isHorizontal) throws NumberFormatException {
        // "~" だけの場合
        if (coordStr.equals("~")) {
            return baseValue; // + (isHorizontal ? 0.5 : 0.0)
        }

        // チルダ記法の処理, "~" + 数値の場合
        if (coordStr.startsWith("~")) {
            coordStr = coordStr.substring(1);
        } else {
            baseValue = 0;
        }

        if (coordStr.matches("-?\\d+")) { // 整数値であれば
            return baseValue + Double.parseDouble(coordStr) + (isHorizontal ? 0.5 : 0.0); // 中心に合わせる
        } else {
            return baseValue + Double.parseDouble(coordStr); // 小数値はそのまま
        }
    }

    public static String locationToString(Location location) {
        int x = location.getBlockX();
        int y = location.getBlockY();
        int z = location.getBlockZ();
        String xStr = Integer.toString(x);
        String yStr = Integer.toString(y);
        String zStr = Integer.toString(z);
        String string = "x:" + xStr + " y:" + yStr + " z:" + zStr;
        return string;
    }
}
