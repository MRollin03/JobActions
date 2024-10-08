package dk.arasbuilds.jobactions.events.listeners;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import dk.arasbuilds.jobactions.events.gui.OrderAccepterGUI;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;

public class MarketGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (event.getView().getTitle().equals(ChatColor.RED + "Job Market")) {
            event.setCancelled(true); // Cancel event by default

            int slot = event.getSlot();
            Player player = (Player) event.getWhoClicked();

            switch (slot) {

                case 9 * 6 - 6:
                    // Implement or call the previous page logic here
                    player.sendMessage(ChatColor.YELLOW + "Previous page functionality not yet implemented.");
                    //TODO: ADD Vault scroll (previous)
                    event.setCancelled(true); // Cancel event by default
                    break;

                case 9 * 6 - 5:
                    player.closeInventory();
                    break;

                case 9 * 6 - 4:
                    // Implement or call the next page logic here
                    player.sendMessage(ChatColor.YELLOW + "Next page functionality not yet implemented.");
                    //TODO: ADD Vault scroll (Next)
                    event.setCancelled(true); // Cancel event by default
                    break;

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
    }
}
