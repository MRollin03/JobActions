package dk.arasbuilds.jobactions.events.listeners;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.database.JobActionsDatabase;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;

public class CompletedOrderVaultGUIListener implements Listener {

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if(event.isRightClick()) {
            event.setCancelled(true);
            return;
        }


        if (event.getView().getTitle().equals(ChatColor.RED + "Job Market")) {
            Player player = (Player) event.getWhoClicked();

            //TODO FIX THE VAULT

            switch (event.getSlot()) {
                case 9 * 4 - 6:
                    // Implement or call the previous page logic here
                    player.sendMessage(ChatColor.YELLOW + "Previous page functionality not yet implemented.");
                    event.setCancelled(true); // Cancel event by default
                    break;

                case 9 * 4 - 5:
                    player.closeInventory();
                    break;

                case 9 * 4 - 4:
                    // Implement or call the next page logic here
                    player.sendMessage(ChatColor.YELLOW + "Next page functionality not yet implemented.");
                    event.setCancelled(true); // Cancel event by default
                    break;

            }
        }
    }
}
