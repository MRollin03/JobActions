package dk.arasbuilds.jobactions.events.gui;

import dk.arasbuilds.jobactions.JobActions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Queue;

public class CompletedOrderVaultGUI implements Listener {

    private static final int INVENTORY_ROWS = 4;
    private static final int INVENTORY_SIZE = INVENTORY_ROWS * 9;
    private static final int MAX_ITEMS_DISPLAY = 3 * 9;
    private static final String GUI_TITLE = ChatColor.RED + "Completed Orders Vault";
    private static Queue<ItemStack> itemsQueue = new LinkedList<>();

    public static void DisplayGUI(Player player) {
        // Create the inventory for the player
        Inventory inv = Bukkit.createInventory(player, INVENTORY_SIZE, GUI_TITLE);

        // Load the player's items and initialize the queue
        List<ItemStack> itemList = JobActions.getInstance().getJobActionsDatabase().loadPlayerItems(player.getUniqueId());
        itemsQueue.clear();
        itemsQueue.addAll(itemList);

        // Add items from the queue to the inventory, up to a maximum of 27 (9*3) items
        for (int i = 0; i < Math.min(itemsQueue.size(), MAX_ITEMS_DISPLAY); i++) {
            inv.setItem(i, itemsQueue.poll());
        }

        addNavigationItems(inv);
        player.openInventory(inv);
    }

    private static void addNavigationItems(Inventory inv) {
        // Previous item
        ItemStack previousItem = createItem(Material.BLUE_WOOL, ChatColor.BLUE + "Previous");
        inv.setItem(INVENTORY_SIZE - 6, previousItem);

        // Close item
        ItemStack closeItem = createItem(Material.RED_WOOL, ChatColor.RED + "Close");
        inv.setItem(INVENTORY_SIZE - 5, closeItem);

        // Next item
        ItemStack nextItem = createItem(Material.GREEN_WOOL, ChatColor.GREEN + "Next");
        inv.setItem(INVENTORY_SIZE - 4, nextItem);
    }

    private static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        List<ItemStack> itemLeftList = new ArrayList<>();

        // Check if the inventory title matches the GUI title
        if (event.getView().getTitle().equals(GUI_TITLE)) {
            // Collect remaining items, except the last row (navigation items)
            for (int i = 0; i < event.getInventory().getSize() - 9; i++) {
                ItemStack item = event.getInventory().getItem(i);
                if (item != null) {
                    itemLeftList.add(item);
                }
            }

            // Synchronize the addition of items to avoid concurrency issues
            synchronized (itemsQueue) {
                if (itemsQueue != null) { // Null check for safety
                    itemsQueue.addAll(itemLeftList);
                }
            }

            // Save remaining items to the database
            JobActions.getInstance().getJobActionsDatabase().savePlayerItems(player.getUniqueId(), new ArrayList<>(itemsQueue));
        }
    }
}
