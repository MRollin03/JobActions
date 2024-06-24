package dk.arasbuilds.jobactions.PluginItems;

import dk.arasbuilds.jobactions.OrdersSerializer;
import org.bukkit.entity.Player;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;

public class JobList {

    private HashSet<Integer> jobIDs = new HashSet<>();
    private ArrayList<PlayerOrders> orderList = new ArrayList<>();
    private OrdersSerializer ordersSerializer;

    public JobList(File filepath) {
        this.ordersSerializer = new OrdersSerializer(filepath);
    }

    public void loadJobList() {
        orderList = ordersSerializer.loadOrders();
        if(orderList == null || orderList.isEmpty()){
            orderList = new ArrayList<>();
        }
    }

    public void save() {
        ordersSerializer.saveOrders(orderList);
    }

    /**
     * Add a new order to the player's orders.
     * @param order the order that needs to be added.
     * @param player the player that created the order.
     */
    public Boolean add(ItemOrder order, Player player) {
        // Get random int for ID that does not match with other IDs
        int randID = (int)(Math.random() * orderList.size());
        while (jobIDs.contains(randID)) {
            randID = (int)(Math.random() * orderList.size());
        }
        jobIDs.add(randID);

        // If player matches with a Player in PlayerOrders, add to their orders
        for (PlayerOrders o : orderList) {
            if (o.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return o.addOrder(order);
            }
        }

        // New player addition
        PlayerOrders newPlayerOrders = new PlayerOrders(player);
        newPlayerOrders.addOrder(order);

        orderList.add(newPlayerOrders);
        return true;
    }

    /**
     * Remove order.
     * @param order the order to remove.
     * @param player the player who created the order.
     */
    public void remove(ItemOrder order, Player player) {
        for (PlayerOrders o : orderList) {
            if (o.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                //TODO make removeal of order function
            }
        }
    }

    public boolean playerExists(Player player) {
        for (PlayerOrders o : orderList) {
            if (o.getPlayer().getUniqueId().equals(player.getUniqueId())) {
                return true;
            }
        }
        return false;
    }

    public ArrayList<PlayerOrders> getOrderList() {
        return orderList;
    }
}
