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

        //Stacks removed
        List<ItemStack> items = new ArrayList<ItemStack>();

        if (!InventoryChecker.hasItems(player, item, amount)) {
            player.sendMessage("You don't have the required amount of " + amount + " items");
            return null;
        }

        int counter = 0;

        for (int i = 0; i < inventory.getSize(); i++) {
            ItemStack currentItem = inventory.getItem(i);

            if (currentItem == null || currentItem.getType() != item) {
                continue;
            }

            int slotAmount = currentItem.getAmount();

            if (counter + slotAmount > amount) {
                currentItem.setAmount(slotAmount - (amount - counter));
                items.add(new ItemStack(item, (amount - counter)));
                break;
            } else {
                counter += slotAmount;
                inventory.setItem(i, null);
                items.add(new ItemStack(item, item.getMaxStackSize()));
            }

            if (counter >= amount) {
                break;
            }
        }
        return items;
    }
}
