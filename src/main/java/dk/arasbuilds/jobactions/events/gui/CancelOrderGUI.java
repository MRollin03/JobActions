package dk.arasbuilds.jobactions.events.gui;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import dk.arasbuilds.jobactions.Utils.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class CancelOrderGUI {


    private static final int INVENTORY_ROWS = 4;
    private static final int INVENTORY_SIZE = INVENTORY_ROWS * 9;
    private static final String GUI_TITLE = ChatColor.RED + "Completed Orders Vault";
    private static final Map<UUID, Queue<ItemStack>> playerItemsQueue = new HashMap<>();


    /**
     * Main function for stating Gui for the Market
     * @param player for the player opening the market
     */
    public static void Display(Player player){
        JobActions plugin = JobActions.getInstance();
        plugin.debug(player.getName() + ": Opened cancel GUI");
        player.closeInventory();

        //Create Inventory GUI
        Inventory inv = Bukkit.createInventory( player,9 * 6, ChatColor.RED + "Cancel Orders");
        ArrayList<ItemOrder> jobs = JobActions.getInstance().getJobActionsDatabase().getOrdersByPlayer(player.getUniqueId());

        if(jobs.size() > 0){
            for (int i = 0; i < Math.min(jobs.size(), 9 * (6 - 1)); i++) {
                ItemOrder order = jobs.get(i);
                ItemStack item = new ItemStack(order.getMaterial());

                ItemMeta meta = item.getItemMeta();

                // set Display name
                meta.setDisplayName(ChatColor.LIGHT_PURPLE
                        + "Order by "+ Bukkit.getOfflinePlayer(order.getUuid()).getName());

                // Set lore to item
                List<String> lore = new ArrayList<String>();
                lore.add(ChatColor.BLUE + "Amount: " + order.getAmount() + " " + order.getMaterial());
                lore.add(ChatColor.GOLD + "Payment: " + order.getPrice() + " " + VaultHook.getEconomyCurrency());
                lore.add(order.getOrderID());
                meta.setLore(lore);

                item.setItemMeta(meta);
                inv.setItem(i, item);
            }
        }

        player.openInventory(inv);
    }


    /**
     * Main function for stating Gui for the accept window
     * @param player for the player opening the vault
     */
    public static void DisplayAccepter(ItemOrder order, Player player) {
        Inventory inv = Bukkit.createInventory(player, 9, ChatColor.AQUA + "Order Cancel Accepter");
        CreateItemStacks(inv, order);
        player.openInventory(inv);
    }

    /**
     * Setups the buttons
     * @param inv inventory where buttons are located
     * @param order regarding the completion
     */
    private static void CreateItemStacks(Inventory inv, ItemOrder order) {
        // Create and set order item
        ItemStack orderItem = new ItemStack(order.getMaterial());
        ItemMeta orderMeta = orderItem.getItemMeta();
        if (orderMeta != null) {
            orderMeta.setDisplayName(ChatColor.AQUA + "" + order.getAmount() + " Pieces");

            // Set lore to item
            List<String> lore = new ArrayList<String>();
            lore.add(Objects.requireNonNull(Bukkit.getOfflinePlayer(order.getUuid())).getName());
            lore.add(order.getMaterial().name() + " x " + order.getAmount());
            lore.add(order.getOrderID());
            orderMeta.setLore(lore);

            orderItem.setItemMeta(orderMeta);
        }
        inv.setItem(4, orderItem);

        // Create and set accepts item
        ItemStack acceptItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta acceptMeta = acceptItem.getItemMeta();
        if (acceptMeta != null) {
            acceptMeta.setDisplayName(ChatColor.GREEN + "ACCEPT");
            acceptItem.setItemMeta(acceptMeta);
        }
        inv.setItem(0, acceptItem);

        // Create and set close item
        ItemStack closeItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta closeMeta = closeItem.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(ChatColor.RED + "CLOSE");
            closeItem.setItemMeta(closeMeta);
        }
        inv.setItem(8, closeItem);
    }

}
