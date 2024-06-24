package dk.arasbuilds.jobactions.PluginItems;
import dk.arasbuilds.jobactions.JobActions;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.UUID;

public class ItemOrder {

    private UUID uuid;
    private Material material;
    private int amount;
    private int price;
    private int orderID = 0;


    public ItemOrder(Player player, Material material, int amount, int price){

        int MaximumStacks;
        if (material.getMaxStackSize() * JobActions.getInstance().getOrderLimit() < amount){
            player.sendMessage("The maximum stack size is " + material.getMaxStackSize() + ".");
        }

        //Setting Order Details
        this.uuid = player.getUniqueId();
        this.material = material;
        this.amount = amount;
        this.price = price;

    }


    //For Editing price on order
    public void setPrice(int price) {
        this.price = price;
    }

    public int getAmount() {
        return amount;
    }

    public Material getMaterial() {
        return material;
    }

    public UUID getUuid() {
        return uuid;
    }

    public int getPrice() { return price; }

    public int getOrderID() { return orderID; }
    public void setOrderID(int orderID) { this.orderID = orderID; }

    @Override
    public String toString() {
        return "" + Bukkit.getPlayer(uuid) + " Requests: " + amount + " " + material + " " + price + " " + orderID;
    }
}
