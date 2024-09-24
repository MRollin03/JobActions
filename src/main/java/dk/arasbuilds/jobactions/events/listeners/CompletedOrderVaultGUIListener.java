package dk.arasbuilds.jobactions.events.listeners;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.*;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.PlayerInventory;


public class CompletedOrderVaultGUIListener implements Listener {

    @EventHandler (priority = EventPriority.LOWEST, ignoreCancelled = true)
        public void onInventoryClick(InventoryClickEvent event) {

        if(event.getView().getTitle().equals(ChatColor.RED + "Completed Orders Vault")
        ){

            Player player = (Player) event.getWhoClicked();


            Inventory clickedInventory = event.getClickedInventory();
            InventoryView view = event.getView();
            Inventory topInventory = view.getTopInventory();

            // Check if the top inventory is a chest and clicked inventory is not null
            if (topInventory != null && topInventory.getType() == InventoryType.CHEST && clickedInventory != null) {
                // Check if the click is in the player's inventory
                if (clickedInventory instanceof PlayerInventory) {
                    // Prevent moving items from the player's inventory to the chest
                    event.setCancelled(true);
                }
                // Check if the click is in the chest
                else if (clickedInventory.getType() == InventoryType.CHEST) {
                    // Allow taking items from the chest (do nothing)
                }

                switch (event.getSlot()) {
                    case 9 * 4 - 6:
                        // Implement or call the previous page logic here
                        player.sendMessage(ChatColor.YELLOW + "Previous page functionality not yet implemented.");
                        //TODO: ADD Vault scroll (previous)
                        event.setCancelled(true); // Cancel event by default
                        break;

                    case 9 * 4 - 5:
                        player.closeInventory();
                        break;

                    case 9 * 4 - 4:
                        // Implement or call the next page logic here
                        player.sendMessage(ChatColor.YELLOW + "Next page functionality not yet implemented.");
                        //TODO: ADD Vault scroll (Next)
                        event.setCancelled(true); // Cancel event by default
                        break;

                }


            }
        }

    }
}

