package dk.arasbuilds.jobactions.events.gui;

import dk.arasbuilds.jobactions.JobActions;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CompletedOrderVaultGUI implements Listener {

    private static final int INVENTORY_ROWS = 4;
    private static final int INVENTORY_SIZE = INVENTORY_ROWS * 9;
    private static final int MAX_ITEMS_DISPLAY = 3 * 9;
    private static final String GUI_TITLE = ChatColor.RED + "Completed Orders Vault";
    private static final Map<UUID, Queue<ItemStack>> playerItemsQueueMap = new HashMap<>();

    public static void DisplayGUI(Player player) {
        // Create the inventory for the player
        Inventory inv = Bukkit.createInventory(player, INVENTORY_SIZE, GUI_TITLE);

        // Load the player's items and initialize the queue
        List<ItemStack> itemList = JobActions.getInstance().getJobActionsDatabase().loadPlayerItems(player.getUniqueId());
        Queue<ItemStack> itemsQueue = new LinkedList<>(itemList);

        System.out.println(itemsQueue + "AAAAAAAAAAAAAAA" );

        // Store the queue for later access
        playerItemsQueueMap.put(player.getUniqueId(), itemsQueue);

        System.out.println("Items queue size: " + itemsQueue.size());

        // Add items from the queue to the inventory, up to a maximum of 27 (9*3) items
        for (int i = 0; i < Math.min(itemsQueue.size(), MAX_ITEMS_DISPLAY); i++) {
            inv.setItem(i, itemsQueue.poll());
            System.out.println(i + ": " + itemsQueue.size());
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
        UUID playerUUID = player.getUniqueId();

        // Retrieve the player's item queue
        Queue<ItemStack> itemsQueue = playerItemsQueueMap.get(playerUUID);

        if (event.getView().getTitle().equals(GUI_TITLE)) {
            List<ItemStack> itemLeftList = new ArrayList<>();

            // Collect remaining items, except the last row (navigation items)
            for (int i = 0; i < event.getInventory().getSize() - 9; i++) {
                ItemStack item = event.getInventory().getItem(i);
                if (item != null) {
                    itemLeftList.add(item);
                }
            }

            // Synchronize and save remaining items
            synchronized (itemsQueue) {
                itemsQueue.addAll(itemLeftList);
            }

            // Save remaining items to the database
            JobActions.getInstance().getJobActionsDatabase().savePlayerItems(playerUUID, new ArrayList<>(itemsQueue));
        }

        // Remove the player's queue from the map
        playerItemsQueueMap.remove(playerUUID);
    }
}

