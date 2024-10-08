package dk.arasbuilds.jobactions.commands;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import dk.arasbuilds.jobactions.database.JobActionsDatabase;
import dk.arasbuilds.jobactions.events.gui.CompletedOrderVaultGUI;
import dk.arasbuilds.jobactions.events.gui.MarketGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OrderCommand implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {return false;}
        Player player = (Player) commandSender;

        //if there are no arguments
        if(args.length == 0 ){ JobActions.getInstance().help(player); return true; }

        switch(args[0].toLowerCase()) {
            case "vault": {
                if(args.length != 1) { JobActions.getInstance().help(player); return true; }
                if (!player.hasPermission("JobActions.vault")) {
                    player.sendMessage(ChatColor.RED + "You do not have JobActions.received permissons");
                    return false;
                }
                CompletedOrderVaultGUI.DisplayGUI(player);
                return true;
            }

            case "cancel": {
                if(args.length != 2) {JobActions.getInstance().help(player); return true;}

                if (!player.hasPermission("Jobactions.cancel")) {
                    player.sendMessage(ChatColor.RED + "You do not have the Jobactions.cancel permissions");
                    return true;
                }

                String orderId = args[1];
                JobActionsDatabase db = JobActions.getInstance().getJobActionsDatabase();
                ItemOrder order = db.getOrderById(orderId);

                if (order == null) {
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
                    JobActions.getInstance().help(player);
                    return true;
                }
                int quantity = Integer.parseInt(args[2]);
                int price = Integer.parseInt(args[3]);
                JobActions plugin = JobActions.getInstance();

                if(price < 0){
                    player.sendMessage(ChatColor.RED + "Payment can't be negative.");
                }

                //Price and order amount can't be zero or negative
                if(plugin.isOrderBaseFeeActivated() && !plugin.isOrderFeePercentageActivated()){
                    if (price < plugin.getOrderBaseFee() && price < 1) {
                        commandSender.sendMessage(ChatColor.RED + "Please enter price larger than the fee = " + Math.ceil(plugin.getOrderBaseFee()));
                        return true;
                    }
                }

                if(plugin.isOrderFeePercentageActivated() && plugin.isOrderBaseFeeActivated()){
                    if (price < Math.ceil(plugin.getOrderBaseFee() + (price * (0.01 * plugin.getOrderFeePercentage()) ) ) && price < 1) {
                        commandSender.sendMessage(ChatColor.RED + "Please enter price larger than the fee = " + Math.ceil(plugin.getOrderBaseFee() + (price * (0.01 * plugin.getOrderFeePercentage()) ) ) );
                        return true;
                    }
                }



                //Limit the amount of orders pl player
                int currentOrderAmount = plugin.getJobActionsDatabase().getOrdersByPlayer(player.getUniqueId()).size();
                if (currentOrderAmount > plugin.getOrderLimit()) {
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
                if (material.getMaxStackSize() * plugin.getOrderLimitStackCount() < quantity) {
                    player.sendMessage("Combined order limit exceeded (max stack size is " + plugin.getOrderLimitStackCount() + ")");
                    return true;
                }

                //Create ItemOrder
                ItemOrder order = new ItemOrder(player, material, quantity, price);
                if (order.getOrderID() == null) {
                    player.sendMessage("Insufficient funds!");
                    return true;
                }

                plugin.getJobActionsDatabase().addOrder(order);
                player.sendMessage(ChatColor.GREEN + "Order created!");
            }

            case "market": {
                if (!player.hasPermission("JobActions.market")) {
                    player.sendMessage(ChatColor.RED + "you don't have permission ´market´");
                    return true;
                }

                MarketGUI.Display(player);
                return true;
            }

            case "reload": {JobActions.getInstance().loadConfig(); player.sendMessage( ChatColor.ITALIC + "[JobActions] Configs Reloaded"); return true;}

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
