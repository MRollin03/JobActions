package dk.arasbuilds.jobactions.PluginItems;

import dk.arasbuilds.jobactions.JobActions;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashMap;

public class PlayerOrders implements Serializable {
    private Player player;
    private ArrayList<ItemOrder> orders;
    private ArrayList<ItemStack> ItemsVault;

    public PlayerOrders(Player player) {
        this.player = player;
        this.orders = new ArrayList<>();
    }

    public ItemOrder getOrderById(String id) {
        for (ItemOrder order : orders) {
            if(order.getOrderID().equals(id)){
                return order;
            }
        }
        return null;
    }

    public Boolean addOrder(ItemOrder order) {
        if(orders == null){orders = new ArrayList<>();}
        if(JobActions.getInstance().getOrderLimit() > orders.size()){
            return orders.add(order);
        }
        else{
            player.sendMessage("The maximum amount of active orders is " + orders.size() + ".");
            return false;
        }
    }

    public void removeOrder(ItemOrder order) {
        orders.remove(order);
    }

    public ArrayList<ItemOrder> getOrders() {
        return orders;
    }

    @Override
    public String toString() {
        return "PlayerOrders{" +
                "player=" + player +
                ", orders=" + orders +
                '}';
    }

    public void clearOrders() {
        orders.clear();
    }

    public void displayOrders() {
        for (ItemOrder order : orders) {
            player.sendMessage(order.toString());
        }
    }

    public ArrayList<ItemOrder> getItemOrders() {return orders;}

    public Player getPlayer() {
        return player;
    }
}
