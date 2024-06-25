package dk.arasbuilds.jobactions.PluginItems;

import dk.arasbuilds.jobactions.JobActions;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.HashMap;

public class PlayerOrders {
    private Player player;
    private ArrayList<ItemOrder> orders;

    PlayerOrders(Player player) {
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

    public ArrayList<ItemOrder> getOrders() {
        return orders;
    }

    public Player getPlayer() {
        return player;
    }
}
