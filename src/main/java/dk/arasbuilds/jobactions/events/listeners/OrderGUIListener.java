package dk.arasbuilds.jobactions.events.listeners;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import dk.arasbuilds.jobactions.PluginItems.JobVerification;
import dk.arasbuilds.jobactions.database.JobActionsDatabase;
import dk.arasbuilds.jobactions.events.gui.GUIUtils;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class OrderGUIListener extends GUIUtils {

    @EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = true)
    public void onInventoryClick(InventoryClickEvent event) {

        if (event.getView().getTitle().equals(ChatColor.AQUA + " Order Accepter ")) {
            event.setCancelled(true);
            JobActions plugin = JobActions.getInstance();
            int slot = event.getSlot();

            switch (slot) {
                case 0:
                    plugin.debug("Attempting accepting order");

                    ItemStack item = event.getInventory().getItem(4);
                    assert item != null;
                    String id = item.getItemMeta().getLore().get(2);

                    plugin.debug("this is the id" + id);
                    ItemOrder order = plugin.getJobActionsDatabase().getOrderById(id);

                    plugin.debug(
                            order.getOrderID()      + " "
                            + order.getAmount()     + " "
                            + order.getPrice()      + " "
                            + order.getMaterial()   + " "
                            + order.getClass()      + " "
                    );

                    JobVerification.finishOrder((Player) event.getWhoClicked() ,order);
                    event.setCancelled(true);
                case 4:
                    event.setCancelled(true);
                case 8:
                    event.setCancelled(true);
                    event.getWhoClicked().closeInventory();

            }

            event.setCancelled(true); // Cancel the event to prevent item moving
        }

        /**
         * CANCEL THE ORDER ACCEPTER GUI LISTENER
         */
        if (event.getView().getTitle().equals(ChatColor.AQUA + "Cancel Order?")) {
            event.setCancelled(true);
            JobActions plugin = JobActions.getInstance();
            Player player = (Player) event.getWhoClicked();
            JobActionsDatabase db = JobActions.getInstance().getJobActionsDatabase();

            int slot = event.getSlot();

            switch (slot) {
                case 0:
                    plugin.debug("Attempting Cancelling order");

                    ItemStack item = event.getInventory().getItem(4);
                    assert item != null;
                    String id = item.getItemMeta().getLore().get(2);

                    plugin.debug("this is the id" + id);
                    ItemOrder order = plugin.getJobActionsDatabase().getOrderById(id);

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
                            event.setCancelled(true);
                            event.getWhoClicked().closeInventory();
                            return;
                        }
                        if (db.removeItemOrder(order)) {
                            player.sendMessage(ChatColor.GREEN + "ItemOrder: " + order.getOrderID() + " has been removed");
                            event.setCancelled(true);
                            event.getWhoClicked().closeInventory();
                            return;
                        } else {
                            player.sendMessage(ChatColor.RED + "An Error Accured while while removing itemorder from market");
                            event.setCancelled(true);
                            event.getWhoClicked().closeInventory();
                            return;
                        }
                    }
                    //remove other players orders
                    else {
                        if (!player.hasPermission("Jobactions.cancel.others")) {
                            player.sendMessage(ChatColor.RED + "You do not have Jobactions.cancel.others");
                            event.setCancelled(true);
                            event.getWhoClicked().closeInventory();
                            return;
                        }
                        if (db.removeItemOrder(order)) {
                            player.sendMessage(ChatColor.GREEN + "ItemOrder: " + order.getOrderID() + " has been removed");
                            event.setCancelled(true);
                            event.getWhoClicked().closeInventory();
                            return;
                        } else {
                            player.sendMessage(ChatColor.RED + "An Error Accured while while removing itemorder from market");
                            event.setCancelled(true);
                            event.getWhoClicked().closeInventory();
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
