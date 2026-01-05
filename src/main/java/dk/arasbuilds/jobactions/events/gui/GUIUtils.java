package dk.arasbuilds.jobactions.events.gui;

import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import dk.arasbuilds.jobactions.Utils.VaultHook;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.event.Listener;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class GUIUtils implements Listener {

    static ItemStack CreateOrderPreviewStack(ItemOrder order) {
        // Create and set order item
            ItemStack itemStack = new ItemStack(order.getMaterial());
            ItemMeta meta = itemStack.getItemMeta();

            // set Display name
            assert meta != null;
            meta.setDisplayName(ChatColor.LIGHT_PURPLE
                    + "Orders by "+ Bukkit.getOfflinePlayer(order.getUuid()).getName());

            // Set lore to item
            List<String> lore = new ArrayList<String>();
            lore.add(ChatColor.BLUE + "Amount: " + order.getAmount() + " " + order.getMaterial());
            lore.add(ChatColor.GOLD + "Payment: " + order.getPrice() + " " + VaultHook.getEconomyCurrency());
            lore.add(order.getOrderID());
            meta.setLore(lore);

            itemStack.setItemMeta(meta);

        return itemStack;
    }

    static ItemStack CreateAcceeptStack(){
        // Create and set accepts item
        ItemStack acceptItem = new ItemStack(Material.EMERALD_BLOCK);
        ItemMeta acceptMeta = acceptItem.getItemMeta();

        if (acceptMeta != null) {
            acceptMeta.setDisplayName(ChatColor.GREEN + "ACCEPT");
            acceptItem.setItemMeta(acceptMeta);
        }
       return acceptItem;
    }

    static ItemStack CreateCancelStack(){
        // Create and set close item
        ItemStack closeItem = new ItemStack(Material.REDSTONE_BLOCK);
        ItemMeta closeMeta = closeItem.getItemMeta();

        if (closeMeta != null) {
            closeMeta.setDisplayName(ChatColor.RED + "CLOSE");
            closeItem.setItemMeta(closeMeta);
        }
        return closeItem;
    }

    /**
     * Setups the buttons
     * @param inv inventory where buttons are located
     * @param order regarding the completion
     */
    @Deprecated
    static void CreateItemStacks(Inventory inv, ItemOrder order) {
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
