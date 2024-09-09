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
            VaultHook.withdraw(player, price);
            //Giving order a Unique order ID:
            this.orderID = OrderIDGenerator.generateOrderID();

            //Setting Order Details
            this.uuid = player.getUniqueId();
            this.material = material;
            this.amount = amount;
            this.price = calculatePrice(price);


            JobActions jobActions = JobActions.getInstance();
            jobActions.getJobActionsDatabase().addOrder(this);
    }

    private int calculatePrice(int price){
        // Calculate the total price:
        // Apply the order fee percentage and base fee to the base price
        double fee = 0;

        if(JobActions.getInstance().isOrderFeePercentageActivated())
        {
            fee =+ price * (1 - JobActions.getInstance().getOrderFeePercentage() / 100.0);
        }
        if(JobActions.getInstance().isOrderBaseFeeActivated())
        {
            fee =+ JobActions.getInstance().getOrderBaseFee();
        }
        System.out.println("fee is: " + fee);
        fee = price - fee;
        System.out.println("Quote is:" + fee);
        return (int) fee;
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
