package dk.arasbuilds.jobactions.commands;

import dk.arasbuilds.jobactions.events.gui.MarketGUI;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class MarketCommand implements CommandExecutor {
    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] strings) {
        if (!(commandSender instanceof Player)) {
            return false;
        }
        Player player = (Player) commandSender;
        if (!player.hasPermission("market")) {
            player.sendMessage(ChatColor.RED + "you don't have permission ´market´");
            return true;
        }

        MarketGUI.Display(player);
        return true;

    }
}