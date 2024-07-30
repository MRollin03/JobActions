package dk.arasbuilds.jobactions.PluginItems;
import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.Utils.OrderIDGenerator;
import dk.arasbuilds.jobactions.Utils.VaultHook;
import dk.arasbuilds.jobactions.database.JobActionsDatabase;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;

import java.io.Serializable;
import java.util.UUID;

public class ItemOrder implements Serializable {

    private UUID uuid;
    private Material material;
    private int amount;
    private int price;
    private String orderID = null;

    //For SQL ItemOrder constructor
    public ItemOrder(OfflinePlayer player, Material material, int amount, int price, String orderID){
        //Giving order a Unique order ID:
        this.orderID = orderID;
        this.uuid = player.getUniqueId();
        this.material = material;
        this.amount = amount;
        this.price = price;
    }

    public ItemOrder(OfflinePlayer player, Material material, int amount, int price){

        if(VaultHook.getBalance(player) < price){ return;}


        if (material.getMaxStackSize() * JobActions.getInstance().getOrderLimit() < amount){
            if(player.isOnline()){
                Player onlinePlayer = player.getPlayer();
                assert onlinePlayer != null;
                onlinePlayer.sendMessage("The maximum stack size is " + material.getMaxStackSize() + ".");
            }
            return;
        }

            //Giving order a Unique order ID:
            this.orderID = OrderIDGenerator.generateOrderID();

            //Setting Order Details
            this.uuid = player.getUniqueId();
            this.material = material;
            this.amount = amount;

            // Calculate the total price:
            // Apply the order fee percentage and base fee to the base price
            double feePercentage = JobActions.getInstance().getOrderFeePercentage() / 100.0;
            int baseFee = JobActions.getInstance().getOrderBaseFee();
            this.price = (int) ((price * (1 - feePercentage)) - baseFee);

            VaultHook.withdraw(player, price);
            JobActions jobActions = JobActions.getInstance();
            jobActions.getJobActionsDatabase().addOrder(this);
    }


    //For Editing price on order
    public void setPrice(int price) {
        this.price = price;
        //TODO PRICE EDITING?
    }

    public void setOrderID(String orderID) {
        this.orderID = orderID;
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

    public String getOrderID() { return orderID; }

    @Override
    public String toString() {
        return "" + Bukkit.getOfflinePlayer(uuid) + " Requests: " + amount + " " + material + " " + price + " " + orderID;
    }
}
