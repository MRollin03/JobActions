package dk.arasbuilds.jobactions.events.gui;

import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class OrderAccepterGUI {

    public static void DisplayGUI(ItemOrder order, Player player) {
        Inventory inv = Bukkit.createInventory(player, 9, ChatColor.AQUA + " Order Accepter ");
        CreateItemStacks(inv, order);
        player.openInventory(inv);
    }

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
