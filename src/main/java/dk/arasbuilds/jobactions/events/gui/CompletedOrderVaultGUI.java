package dk.arasbuilds.jobactions.events.gui;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.Utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CompletedOrderVaultGUI implements Listener {

    private static final int INVENTORY_ROWS = 4;
    private static final int INVENTORY_SIZE = INVENTORY_ROWS * 9;
    private static final String GUI_TITLE = ChatColor.RED + "Completed Orders Vault";
    private static final Map<UUID, Queue<ItemStack>> playerItemsQueue = new HashMap<>();

    /**
     * Main function for stating Gui for the Vault
     * @param player for the player opening the vault
     */
    public static void DisplayGUI(Player player) {
        // Create the inventory for the player
        Inventory inv = Bukkit.createInventory(player, INVENTORY_SIZE, GUI_TITLE);

        // Load the player's items and initialize the queue
        List<ItemStack> itemList = JobActions.getInstance().getJobActionsDatabase().getPlayerVault(player.getUniqueId());
        itemList = ItemStackUtils.compressItemStacks(itemList);
        Queue<ItemStack> itemsQueue = new LinkedList<>(itemList);

        System.out.println("Loaded items: " + itemList.size());

        // Store the queue for later access
        playerItemsQueue.put(player.getUniqueId(), itemsQueue);

        System.out.println("Items queue size: " + itemsQueue.size());

        // Add items from the queue to the inventory, up to a maximum of INVENTORY_SIZE items
        int index = 0;
        while (!itemsQueue.isEmpty() && index < 9 * (INVENTORY_ROWS-1)) {
            ItemStack item = itemsQueue.poll();
            inv.setItem(index, item);
            index++;
            System.out.println("Added item at index " + (index - 1) + ": " + item);
        }

        addNavigationItems(inv);
        player.openInventory(inv);
    }

    /**
     * Adds navigation buttons to the GUI
     * @param inv
     */
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

    /**
     * Create item for the buttons
     * @param material material button
     * @param name  name of button
     * @return Itemstack / button
     */
    private static ItemStack createItem(Material material, String name) {
        ItemStack item = new ItemStack(material);
        ItemMeta meta = item.getItemMeta();
        if (meta != null) {
            meta.setDisplayName(name);
            item.setItemMeta(meta);
        }
        return item;
    }

    /**
     * When closing inventory the GUI items will be added to the database
     * @param event
     */
    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {
        Player player = (Player) event.getPlayer();
        UUID playerUUID = player.getUniqueId();

        // Retrieve the player's item queue
        Queue<ItemStack> itemsQueue = new LinkedList<>(playerItemsQueue.getOrDefault(playerUUID, new LinkedList<>()));

        if (event.getView().getTitle().equals(GUI_TITLE)) {
            List<ItemStack> itemLeftList = new ArrayList<>();

            // Collect remaining items, excluding the last row (navigation items)
            int inventorySize = event.getInventory().getSize();
            for (int i = 0; i < inventorySize - 9; i++) {
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
            JobActions.getInstance().getJobActionsDatabase().clearPlayerVault(playerUUID);
            JobActions.getInstance().getJobActionsDatabase().addStacksToVault(playerUUID, new ArrayList<>(itemsQueue));
        }

        // Remove the player's queue from the map
        playerItemsQueue.remove(playerUUID);
    }

    /**
     * Safe way to add a Collection of items to a players vault without losing items
     * under the process.
     * @param uuid player uuid
     * @param items collection of items to add
     */
    public static void SafeAddItemToPlayerVault(UUID uuid, Collection<ItemStack> items){
        JobActions jobactions = JobActions.getInstance();
        OfflinePlayer player  = Bukkit.getOfflinePlayer(uuid);

        //if online close inventory
        if(player.isOnline()){
            player.getPlayer().closeInventory();
        }
        List<ItemStack> vaultItems = jobactions.getJobActionsDatabase().getPlayerVault(uuid);

        assert items != null;
        vaultItems.addAll(items);

        jobactions.getJobActionsDatabase().clearPlayerVault(uuid);
        jobactions.getJobActionsDatabase().addStacksToVault(uuid, vaultItems);

    }

}

