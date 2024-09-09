package dk.arasbuilds.jobactions.Utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.ArrayList;
import java.util.List;


public class InventoryManipulator {

    public static void addItemsToInventory(Player player, ItemStack item, int amount) {
        Inventory inventory = player.getInventory();
        int maxStackSize = item.getMaxStackSize();

        while (amount > 0) {
            ItemStack addItem = item.clone();
            int addAmount = Math.min(amount, maxStackSize);
            addItem.setAmount(addAmount);
            inventory.addItem(addItem);
            amount -= addAmount;
        }
    }

    public static List<ItemStack> removeItemsFromInventory(Player player, Material item, int amount) {
        Inventory inventory = player.getInventory();

        // Stacks removed
        List<ItemStack> items = new ArrayList<>();

        // Check if the player has enough items
        if (!InventoryChecker.hasItems(player, item, amount)) {
            player.sendMessage("You don't have the required amount of " + amount + " items.");
            return null;
        }

        int counter = 0; // Track the number of items removed

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack currentItem = inventory.getItem(i);

            // Skip if the current item is null or is not the material we want
            if (currentItem == null || currentItem.getType() != item) {
                continue;
            }

            int slotAmount = currentItem.getAmount(); // Get the amount in the current slot

            // If this stack contains more than the remaining amount needed
            if (counter + slotAmount > amount) {
                int toRemove = amount - counter; // Calculate the remaining items to remove
                currentItem.setAmount(slotAmount - toRemove); // Decrease the stack size
                items.add(new ItemStack(item, toRemove)); // Add the exact amount removed
                break; // Stop once we've removed enough items
            } else {
                // Add the full stack to the removed items
                items.add(new ItemStack(item, slotAmount));
                counter += slotAmount; // Update the counter with the number of items removed
                inventory.setItem(i, null); // Clear the slot
            }

            // Stop if we've removed enough items
            if (counter >= amount) {
                break;
            }
        }

        return items;
    }

}
