package dk.arasbuilds.jobactions.Utils;

import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

public class InventoryChecker {

    // Check if the player's inventory contains the exact item
    public static boolean hasItem(Player player, ItemStack item) {
        if (item == null || item.getType() == Material.AIR) {
            return false;
        }

        Inventory inventory = player.getInventory();
        for (ItemStack invItem : inventory.getContents()) {
            if (invItem != null
                    && invItem.getType() == item.getType()
                    && invItem.getAmount() == item.getAmount()
                    && (item.hasItemMeta() ? invItem.hasItemMeta() && invItem.getItemMeta().equals(item.getItemMeta()) : !item.hasItemMeta())) {
                return true;
            }
        }
        return false;
    }

    // Check if the player's inventory has at least a certain amount of a material
    public static boolean hasItems(Player player, Material material, int amount) {
        int itemAmount = amountOfItem(player, material);
        if (itemAmount >= amount) {
            return true;
        }
        player.sendMessage("You don't have the required items");
        return false;
    }

    // Count the total amount of a specific material in the player's inventory
    public static int amountOfItem(Player player, Material material) {
        if (material == null) {
            return 0;
        }

        Inventory inventory = player.getInventory();
        int counter = 0;
        for (ItemStack invItem : inventory.getContents()) {
            if (invItem != null && invItem.getType() == material) {
                counter += invItem.getAmount();
            }
        }
        return counter;
    }
}
