package dk.arasbuilds.jobactions.events.listeners;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import dk.arasbuilds.jobactions.PluginItems.JobVerification;
import dk.arasbuilds.jobactions.database.JobActionsDatabase;
import dk.arasbuilds.jobactions.events.gui.OrderAccepterGUI;
import org.bukkit.ChatColor;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class CancelOrderGUIListener {


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.RED + "Cancel Orders")) {
            event.setCancelled(true); // Cancel event by default

            int slot = event.getSlot();
            Player player = (Player) event.getWhoClicked();

            switch (slot) {

                default:

                    ItemStack clickedItem = event.getCurrentItem();
                    if (clickedItem == null || !clickedItem.hasItemMeta()) {
                        return; // Cancelled event if item is null or has no meta
                    }

                    ItemMeta itemMeta = clickedItem.getItemMeta();
                    if (!itemMeta.hasLore()) {
                        return; // Cancelled event if item has no lore
                    }

                    List<String> lore = itemMeta.getLore();

                    if (lore == null || lore.isEmpty()) {
                        return; // Cancelled event if lore is null or empty
                    }
                    String id = lore.get(2);
                    ItemOrder order = JobActions.getInstance().getJobActionsDatabase().getOrderById(id);
                    OrderAccepterGUI.DisplayGUI(order, player);

                    break;
            }
        }

        if(event.getView().getTitle().equals(ChatColor.AQUA + "Order Cancel Accepter")){
            JobActions plugin = JobActions.getInstance();
            Player player = (Player) event.getWhoClicked();
            int slot = event.getSlot();

            switch (slot) {
                case 0:
                    plugin.debug("Attempting canceling order");

                    // Get itemstack with order infomation
                    ItemStack item = event.getInventory().getItem(4);
                    assert item != null;

                    ItemStack clickedItem = event.getCurrentItem();
                    if (clickedItem == null || !clickedItem.hasItemMeta()) {
                        return; // Cancelled event if item is null or has no meta
                    }

                    ItemMeta itemMeta = clickedItem.getItemMeta();
                    if (!itemMeta.hasLore()) {
                        return; // Cancelled event if item has no lore
                    }

                    List<String> lore = itemMeta.getLore();

                    if (lore == null || lore.isEmpty()) {
                        return; // Cancelled event if lore is null or empty
                    }
                    String orderId = lore.get(2);

                    //Get the item order if the order exists
                    JobActionsDatabase db = JobActions.getInstance().getJobActionsDatabase();
                    ItemOrder order = db.getOrderById(orderId);

                    //If no order received cancel event
                    if (order == null) {
                        player.sendMessage(ChatColor.RED + "Order does not exist or orderid is wrong");
                        event.setCancelled(true);
                        return;
                    }

                    //Debug: Infomation about the itemorder recived.
                    plugin.debug(
                            order.getOrderID()      + " "
                                    + order.getAmount()     + " "
                                    + order.getPrice()      + " "
                                    + order.getMaterial()   + " "
                                    + order.getClass()      + " "
                    );

                    //remove player's own orders
                    if (order.getUuid().equals(player.getUniqueId())) {
                        if (!player.hasPermission("Jobactions.cancel.self")) {
                            player.sendMessage(ChatColor.RED + "You do not have Jobactions.cancel.self");
                            return;
                        }
                        if (db.removeItemOrder(order)) {
                            player.sendMessage(ChatColor.GREEN + "ItemOrder: " + order.getOrderID() + " has been removed");
                            return;
                        } else {
                            player.sendMessage(ChatColor.RED + "An Error Accured while while removing itemorder from market");
                            return;
                        }
                    }
                    //remove other players orders
                    else {
                        if (!player.hasPermission("Jobactions.cancel.others")) {
                            player.sendMessage(ChatColor.RED + "You do not have Jobactions.cancel.others");
                            return;
                        }
                        if (db.removeItemOrder(order)) {
                            player.sendMessage(ChatColor.GREEN + "ItemOrder: " + order.getOrderID() + " has been removed");
                            return;
                        } else {
                            player.sendMessage(ChatColor.RED + "An Error Accured while while removing itemorder from market");
                            return;
                        }
                    }
                case 4:
                    event.setCancelled(true);
                case 8:
                    event.setCancelled(true);
                    event.getWhoClicked().closeInventory();

            }

            event.setCancelled(true); // Cancel the event to prevent item moving
        }
    }

}
