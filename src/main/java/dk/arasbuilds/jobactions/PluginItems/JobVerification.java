package dk.arasbuilds.jobactions.PluginItems;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.Utils.InventoryChecker;
import dk.arasbuilds.jobactions.Utils.InventoryManipulator;
import dk.arasbuilds.jobactions.Utils.VaultHook;
import dk.arasbuilds.jobactions.events.gui.CompletedOrderVaultGUI;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.HumanEntity;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.util.List;
import java.util.Stack;

public class JobVerification {

    public static void finishOrder(Player player,  ItemOrder order){

        //check if player has permission to do orders
        if(!player.hasPermission("JobActions.completeOrder")){
            player.sendMessage("you don't have permission to complete orders");
            return;
        }

        //Check if it's the players own order
        if(player == Bukkit.getOfflinePlayer(order.getUuid())){
            player.sendMessage("You can't complete your own order");
            return;
        }

        //Check if player has required items
        if(!InventoryChecker.hasItems(player, order.getMaterial(), order.getAmount())){
            return;
        }

            //Give player Money
            VaultHook.deposit(player, order.getPrice());

            //get database
            JobActions jobactions = JobActions.getInstance();
            jobactions.getJobActionsDatabase().removeItemOrder(order);

            List<ItemStack> removedItems = InventoryManipulator.removeItemsFromInventory(player, order.getMaterial(), order.getAmount());
            CompletedOrderVaultGUI.SafeAddItemToPlayerVault(order.getUuid(), removedItems);

            //send players messages
            if(Bukkit.getOfflinePlayer(order.getUuid()).isOnline()){
                Bukkit.getPlayer(order.getUuid()).sendMessage(ChatColor.GREEN + "" + player.getName() +" have completed the order");
            }
            player.sendMessage(ChatColor.GREEN + "Order finished, you received " + order.getPrice() + " " + VaultHook.getEconomyCurrency());


    }




}
