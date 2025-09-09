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

public class OrderAccepterGUI extends GUIUtils{

    /**
     * Main function for stating Gui for the accept window
     * @param player for the player opening the vault
     */
    public static void DisplayGUI(ItemOrder order, Player player) {
        Inventory inv = Bukkit.createInventory(player, 9, ChatColor.AQUA + " Order Accepter ");

        // ORDER PREVIEW STACK
        ItemStack orderPreview = CreateOrderPreviewStack(order);
        inv.setItem(4, orderPreview);

        // ORDER ACCEPT STACK/BUTTON
        inv.setItem(0, CreateAcceeptStack());

        // ORDER CANCEL STACK/BUTTON
        inv.setItem(8, CreateCancelStack());

        player.openInventory(inv);
    }

}
