package dk.arasbuilds.jobactions.commands;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import dk.arasbuilds.jobactions.database.JobActionsDatabase;
import dk.arasbuilds.jobactions.events.gui.CancelOrderGUI;
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
                    player.sendMessage(ChatColor.RED + "You do not have JobActions.received permissions");
                    return false;
                }
                CompletedOrderVaultGUI.DisplayGUI(player);
                return true;
            }

            case "cancel": {
                if(args.length > 2) {JobActions.getInstance().help(player); return true;}
                if(args.length == 1) { CancelOrderGUI.Display(player); JobActions.getInstance().debug("AAAAAAAAAAAAAAAAAa"); return true; }

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
                        player.sendMessage(ChatColor.RED + "You do not have Jobactions.cancel.self");
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
                    if (!player.hasPermission("Jobactions.cancel.others")) {
                        player.sendMessage(ChatColor.RED + "You do not have Jobactions.cancel.others");
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

                // Parse and validate quantity
                int quantity;
                try {
                    quantity = Integer.parseInt(args[2]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid quantity. Please enter a number.");
                    return true;
                }

                if (quantity <= 0) {
                    player.sendMessage(ChatColor.RED + "Quantity must be greater than 0.");
                    return true;
                }

                int price;
                try {
                    price = Integer.parseInt(args[3]);
                } catch (NumberFormatException e) {
                    player.sendMessage(ChatColor.RED + "Invalid price. Please enter a number.");
                    return true;
                }

                if (price <= 0) {
                    player.sendMessage(ChatColor.RED + "Price must be greater than 0.");
                    return true;
                }

                JobActions plugin = JobActions.getInstance();

                // Calculate the fee that will be applied
                double totalFee = 0;

                if (plugin.isOrderFeePercentageActivated()) {
                    totalFee += price * (plugin.getOrderFeePercentage() / 100.0);
                }

                if (plugin.isOrderBaseFeeActivated()) {
                    totalFee += plugin.getOrderBaseFee();
                }

                // Calculate what the final price would be
                double finalPrice = price - totalFee;

                // Check if price covers the fees
                if (finalPrice <= 0) {
                    int minimumPrice = (int) Math.ceil(totalFee) + 1; // Add 1 to ensure positive final price
                    player.sendMessage(ChatColor.RED + "Price too low! Minimum price with current fees: " + minimumPrice);
                    player.sendMessage(ChatColor.YELLOW + "Fee breakdown:");
                    if (plugin.isOrderFeePercentageActivated()) {
                        player.sendMessage(ChatColor.YELLOW + "  - Percentage fee (" + plugin.getOrderFeePercentage() + "%): " +
                                (int)(price * (plugin.getOrderFeePercentage() / 100.0)));
                    }
                    if (plugin.isOrderBaseFeeActivated()) {
                        player.sendMessage(ChatColor.YELLOW + "  - Base fee: " + plugin.getOrderBaseFee());
                    }
                    return true;
                }

                // Limit the amount of orders per player
                int currentOrderAmount = plugin.getJobActionsDatabase().getOrdersByPlayer(player.getUniqueId()).size();
                if (currentOrderAmount >= plugin.getOrderLimit()) {
                    player.sendMessage(ChatColor.RED + "Order limit exceeded (" + currentOrderAmount + "/" + plugin.getOrderLimit() + ")");
                    return true;
                }

                // Get item from hand or requested
                Material material = null;
                if (args[1].equalsIgnoreCase("hand")) {
                    material = player.getInventory().getItemInMainHand().getType();
                    if (material == Material.AIR) {
                        player.sendMessage(ChatColor.RED + "You must hold an item in your hand!");
                        return true;
                    }
                } else {
                    try {
                        material = Material.valueOf(args[1].toUpperCase());
                    } catch (IllegalArgumentException e) {
                        player.sendMessage(ChatColor.RED + "Invalid material: " + args[1]);
                        return true;
                    }
                }

                // Check if item request is above OrderLimitStacks
                int maxAllowedQuantity = material.getMaxStackSize() * plugin.getOrderLimitStackCount();
                if (quantity > maxAllowedQuantity) {
                    player.sendMessage(ChatColor.RED + "Quantity too high! Maximum allowed: " + maxAllowedQuantity +
                            " (" + plugin.getOrderLimitStackCount() + " stacks of " + material.getMaxStackSize() + ")");
                    return true;
                }

                // Create ItemOrder
                ItemOrder order = new ItemOrder(player, material, quantity, price);
                if (order.getOrderID() == null) {
                    player.sendMessage(ChatColor.RED + "Insufficient funds!");
                    return true;
                }

                plugin.getJobActionsDatabase().addOrder(order);

                // Success message with details
                player.sendMessage(ChatColor.GREEN + "Order created successfully!");
                player.sendMessage(ChatColor.GRAY + "Material: " + ChatColor.WHITE + material.name());
                player.sendMessage(ChatColor.GRAY + "Quantity: " + ChatColor.WHITE + quantity);
                player.sendMessage(ChatColor.GRAY + "Price offered: " + ChatColor.WHITE + price);
                if (totalFee > 0) {
                    player.sendMessage(ChatColor.GRAY + "Fee paid: " + ChatColor.WHITE + (int)totalFee);
                    player.sendMessage(ChatColor.GRAY + "Final price: " + ChatColor.WHITE + (int)finalPrice);
                }

                return true;
            }

            case "market": {
                if (!player.hasPermission("JobActions.market")) {
                    player.sendMessage(ChatColor.RED + "you don't have permission ´JobActions.market´");
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
