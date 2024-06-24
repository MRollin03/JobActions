package dk.arasbuilds.jobactions;

import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import dk.arasbuilds.jobactions.PluginItems.PlayerOrders;
import org.bukkit.util.io.BukkitObjectInputStream;
import org.bukkit.util.io.BukkitObjectOutputStream;

import java.io.*;
import java.util.ArrayList;
import java.util.List;

public class OrdersSerializer {
    private File file;

    public OrdersSerializer(File file) {
        this.file = file;
    }

    public void saveOrders(ArrayList<PlayerOrders> orders) {
        try (ObjectOutputStream oos = new BukkitObjectOutputStream(new FileOutputStream(file))) {
            oos.writeObject(orders);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<PlayerOrders> loadOrders() {
        ArrayList<PlayerOrders> orders = new ArrayList<>();
        try (ObjectInputStream ois = new BukkitObjectInputStream(new FileInputStream(file))) {
            orders = (ArrayList<PlayerOrders>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return orders;
    }
}
