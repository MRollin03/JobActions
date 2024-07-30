package dk.arasbuilds.jobactions.events.listeners;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import dk.arasbuilds.jobactions.PluginItems.JobVerification;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;

public class OrderGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.AQUA + " Order Accepter ")) {
            int slot = event.getSlot();

            switch (slot) {
                case 0:
                    System.out.print("Attempting Accepting");

                    ItemStack item = event.getInventory().getItem(4);
                    assert item != null;
                    String id = item.getItemMeta().getLore().get(2);

                    System.out.println("this is the id" + id);
                    ItemOrder order = JobActions.getInstance().getJobActionsDatabase().getOrderById(id);

                    System.out.println(
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
    }
}
