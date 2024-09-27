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

import java.util.ArrayList;
import java.util.List;

public class MarketGUI {

    /**
     * Main function for stating Gui for the Market
     * @param player for the player opening the market
     */
    public static void Display(Player player){
        JobActions plugin = JobActions.getInstance();
        plugin.debug(player.getName() + " opened Market!");
        player.closeInventory();

        //Create Inventory GUI
        Inventory inv = Bukkit.createInventory( player,9 * 6, ChatColor.RED + "Job Market");

        ArrayList<ItemOrder> jobs = JobActions.getInstance().getJobActionsDatabase().getAllItemOrders();
        plugin.debug("amount of orders on market: " + jobs.size());

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

        addNavigationItems(inv);

        player.openInventory(inv);
    }

    /**
     * Adds navigation buttons to the GUI
     * @param inv
     */
    private static void addNavigationItems(Inventory inv) {
        // Previous item
        ItemStack previousItem = new ItemStack(Material.BLUE_WOOL);
        ItemMeta previousMeta = previousItem.getItemMeta();
        if (previousMeta != null) {
            previousMeta.setDisplayName(ChatColor.BLUE + "Previous");
            previousItem.setItemMeta(previousMeta);
        }
        inv.setItem(48, previousItem);

        // Close item
        ItemStack closeItem = new ItemStack(Material.RED_WOOL);
        ItemMeta closeMeta = closeItem.getItemMeta();
        if (closeMeta != null) {
            closeMeta.setDisplayName(ChatColor.RED + "Close");
            closeItem.setItemMeta(closeMeta);
        }
        inv.setItem(49, closeItem);

        // Next item
        ItemStack nextItem = new ItemStack(Material.GREEN_WOOL);
        ItemMeta nextMeta = nextItem.getItemMeta();
        if (nextMeta != null) {
            nextMeta.setDisplayName(ChatColor.GREEN + "Next");
            nextItem.setItemMeta(nextMeta);
        }
        inv.setItem(50, nextItem);
    }

}
