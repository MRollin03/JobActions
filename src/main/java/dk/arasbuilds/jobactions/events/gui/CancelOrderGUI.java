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

public class CancelOrderGUI  extends GUIUtils
{

    private static final int INVENTORY_ROWS = 4;
    private static final int INVENTORY_SIZE = INVENTORY_ROWS * 9;
    private static final String GUI_TITLE = ChatColor.RED + "Cancel Orders";
    private static final Map<UUID, Queue<ItemStack>> playerItemsQueue = new HashMap<>();

    /**
     * Main function for stating Gui for the Market
     * @param player for the player opening the Closing GUI
     */
    public static void Display(Player player){
        JobActions plugin = JobActions.getInstance();
        plugin.debug(player.getName() + ": Opened cancel GUI");
        player.closeInventory();

        //Create Inventory GUI
        Inventory inv = Bukkit.createInventory( player,INVENTORY_SIZE, GUI_TITLE);
        ArrayList<ItemOrder> jobs = JobActions.getInstance().getJobActionsDatabase().getOrdersByPlayer(player.getUniqueId());

        if(jobs.size() > 0){
            for (int i = 0; i < Math.min(jobs.size(), 9 * (6 - 1)); i++) {
                ItemOrder order = jobs.get(i);
                ItemStack itemStack = CreateOrderPreviewStack(order);
                inv.setItem(i, itemStack);
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

}
