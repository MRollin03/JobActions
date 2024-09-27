package dk.arasbuilds.jobactions.commands;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import dk.arasbuilds.jobactions.database.JobActionsDatabase;
import dk.arasbuilds.jobactions.events.gui.CompletedOrderVaultGUI;
import net.milkbowl.vault.chat.Chat;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.block.data.type.Switch;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OrderCreate implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {return false;}
        Player player = (Player) commandSender;

        //if there are no arguments
        if(args.length == 0 ){ JobActions.getInstance().help(player); return true; }

        switch(args[0].toLowerCase()) {
            case "vault": {
                if (!player.hasPermission("JobActions.vault")) {
                    player.sendMessage(ChatColor.RED + "You do not have JobActions.received permissons");
                    return false;
                }
                CompletedOrderVaultGUI.DisplayGUI(player);
                return true;
            }

            case "cancel": {
                if (!player.hasPermission("Jobactions.cancel")) {
                    player.sendMessage(ChatColor.RED + "You do not have the Jobactions.cancel permissions");
                    return true;
                }
                JobActionsDatabase db = JobActions.getInstance().getJobActionsDatabase();
                String orderId = args[1];
                ItemOrder order = db.getOrderById(orderId);
                if (order.equals(null)) {
                    player.sendMessage(ChatColor.RED + "Order does not exist or orderid is wrong");
                    return true;
                }

                //remove player's own orders
                if (order.getUuid().equals(player.getUniqueId())) {
                    if (!player.hasPermission("Jobactions.cancel.self")) {
                        player.sendMessage(ChatColor.RED + "You do not have Joabactions.cancel.self");
                        return true;
                    }
                    if (db.removeItemOrder(order)) {
                        player.sendMessage(ChatColor.GREEN + "ItemOrder: " + order.getOrderID() + " has been removed");
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "An Error Accured while while removing itemorder from market");
                        return true;
                    }
                }
                //remove other players orders
                else {
                    if (!player.hasPermission("jobactions.cancel.others")) {
                        player.sendMessage(ChatColor.RED + "You do not have Joabactions.cancel.others");
                        return true;
                    }
                    if (db.removeItemOrder(order)) {
                        player.sendMessage(ChatColor.GREEN + "ItemOrder: " + order.getOrderID() + " has been removed");
                        return true;
                    } else {
                        player.sendMessage(ChatColor.RED + "An Error Accured while while removing itemorder from market");
                        return true;
                    }
                }
            }

            case "create": {
            /*Create order Logic
            create orders hand or material
            */

                if (!player.hasPermission("JobActions.createorder")) {
                    player.sendMessage(ChatColor.RED + "You do not have Jobactions.createorder permissons");
                    return true;
                }

                if (args.length != 4) {
                    return false;
                }
                int quantity = Integer.parseInt(args[2]);
                int price = Integer.parseInt(args[3]);

                //Price and order amount can't be zero or negative
                if (price < JobActions.getInstance().getOrderBaseFee() || price < 1) {
                    commandSender.sendMessage(ChatColor.RED + "Please enter price larger than the basefee = " + JobActions.getInstance().getOrderBaseFee());
                    return true;
                }

                //Limit the amount of orders pl player
                int currentOrderAmount = JobActions.getInstance().getJobActionsDatabase().getOrdersByPlayer(player.getUniqueId()).size();
                if (currentOrderAmount > JobActions.getInstance().getOrderLimit()) {
                    commandSender.sendMessage(ChatColor.RED + "Order limit exceeded");
                    return true;
                }

                //Get item from hand or requested
                Material material = null;
                if (args[1].equalsIgnoreCase("hand")) {
                    material = player.getInventory().getItemInMainHand().getType();
                } else {
                    try {
                        material = Material.valueOf(args[1].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        player.sendMessage("Invalid material");
                        return true;
                    }
                }

                //Check if item request is above OrderLimitStacks
                if (material.getMaxStackSize() * JobActions.getInstance().getOrderLimitStackCount() < quantity) {
                    player.sendMessage("Combined order limit exceeded (max stack size is " + JobActions.getInstance().getOrderLimitStackCount() + ")");
                    return true;
                }

                //Create ItemOrder
                ItemOrder order = new ItemOrder(player, material, quantity, price);
                if (order.getOrderID() == null) {
                    player.sendMessage("Insufficient funds!");
                    return true;
                }

                JobActions.getInstance().getJobActionsDatabase().addOrder(order);
                player.sendMessage(ChatColor.GREEN + "Order created!");
            }

            case "help":{
                JobActions.getInstance().help(player);
                return true;
            }

            default:{
                JobActions.getInstance().help(player);
                return true;
            }
        }
    }

}
