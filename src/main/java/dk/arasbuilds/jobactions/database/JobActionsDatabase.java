package dk.arasbuilds.jobactions.database;

import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import dk.arasbuilds.jobactions.Utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JobActionsDatabase {

    private final Connection connection;

    public JobActionsDatabase(String path) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        try (Statement statement = connection.createStatement()) {
            statement.execute("""
                CREATE TABLE IF NOT EXISTS player_items (
                    id INT AUTO_INCREMENT PRIMARY KEY,
                    player_uuid VARCHAR(36),
                    item_stack TEXT,
                    stack_size INT
                )
             """);
            // Create item_orders table
            statement.execute("""
                CREATE TABLE IF NOT EXISTS item_orders (
                    order_id TEXT PRIMARY KEY,
                    player_uuid TEXT NOT NULL,
                    material TEXT NOT NULL,
                    amount INTEGER NOT NULL,
                    price INTEGER NOT NULL,
                    FOREIGN KEY(player_uuid) REFERENCES players(uuid)
                )
            """);
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void addPlayer(Player player) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO players (uuid, username) VALUES (?, ?)"
        )) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(UUID uuid) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT * FROM players WHERE uuid = ?"
        )) {
            preparedStatement.setString(1, uuid.toString());
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addOrder(ItemOrder order) {
        try {
            // Check if the order already exists
            if (orderExists(order.getOrderID())) {
                // Update the existing order if necessary
                updateOrder(order);
            } else {
                // Insert new order
                try (PreparedStatement preparedStatement = connection.prepareStatement(
                        "INSERT INTO item_orders (order_id, player_uuid, material, amount, price) VALUES (?, ?, ?, ?, ?)"
                )) {
                    preparedStatement.setString(1, order.getOrderID());
                    preparedStatement.setString(2, order.getUuid().toString());
                    preparedStatement.setString(3, order.getMaterial().toString());
                    preparedStatement.setInt(4, order.getAmount());
                    preparedStatement.setInt(5, order.getPrice());
                    preparedStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean orderExists(String orderID) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT 1 FROM item_orders WHERE order_id = ?"
        )) {
            preparedStatement.setString(1, orderID);
            ResultSet resultSet = preparedStatement.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateOrder(ItemOrder order) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "UPDATE item_orders SET player_uuid = ?, material = ?, amount = ?, price = ? WHERE order_id = ?"
        )) {
            preparedStatement.setString(1, order.getUuid().toString());
            preparedStatement.setString(2, order.getMaterial().toString());
            preparedStatement.setInt(3, order.getAmount());
            preparedStatement.setInt(4, order.getPrice());
            preparedStatement.setString(5, order.getOrderID());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ItemOrder> getOrdersByPlayer(UUID playerUUID) {
        List<ItemOrder> orders = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT order_id, material, amount, price FROM item_orders WHERE player_uuid = ?"
        )) {
            preparedStatement.setString(1, playerUUID.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String orderID = resultSet.getString("order_id");
                Material material = Material.valueOf(resultSet.getString("material"));
                int amount = resultSet.getInt("amount");
                int price = resultSet.getInt("price");

                OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID); // Obtain player instance by UUID
                ItemOrder order = new ItemOrder(player, material, amount, price, orderID);
                order.setOrderID(orderID);
                orders.add(order);
                System.out.println("Added ");
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public ItemOrder getOrderById(String orderID) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT player_uuid, material, amount, price FROM item_orders WHERE order_id = ?"
        )) {
            preparedStatement.setString(1, orderID);
            ResultSet resultSet = preparedStatement.executeQuery();

            if (resultSet.next()) {
                UUID playerUUID = UUID.fromString(resultSet.getString("player_uuid"));
                Material material = Material.valueOf(resultSet.getString("material"));
                int amount = resultSet.getInt("amount");
                int price = resultSet.getInt("price");

                OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
                ItemOrder order = new ItemOrder(player, material, amount, price, orderID);
                order.setOrderID(orderID);
                return order;
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return null; // Return null if no order is found with the given ID
    }

    public boolean removeItemOrder(ItemOrder order) {
        String orderID = order.getOrderID();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "DELETE FROM item_orders WHERE order_id = ?"
        )) {
            preparedStatement.setString(1, orderID);
            int affectedRows = preparedStatement.executeUpdate();
            return affectedRows > 0; // Return true if at least one row was deleted
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public ArrayList<ItemOrder> getAllItemOrders() {
        ArrayList<ItemOrder> orders = new ArrayList<>();
        long startTime = System.currentTimeMillis(); // Start time for performance monitoring
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT order_id, player_uuid, material, amount, price FROM item_orders"
        )) {
            ResultSet resultSet = preparedStatement.executeQuery();
            int count = 0;

            while (resultSet.next()) {
                String orderID = resultSet.getString("order_id");
                UUID playerUUID = UUID.fromString(resultSet.getString("player_uuid"));
                Material material = Material.valueOf(resultSet.getString("material"));
                int amount = resultSet.getInt("amount");
                int price = resultSet.getInt("price");

                OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
                ItemOrder order = new ItemOrder(player, material, amount, price, orderID);
                orders.add(order);

                count++;
            }

            long endTime = System.currentTimeMillis(); // End time for performance monitoring
            System.out.println("Number of orders fetched: " + count);
            System.out.println("Time taken to fetch orders: " + (endTime - startTime) + " ms");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    public void savePlayerItems(UUID playerUUID, List<ItemStack> itemStacks) {
        try {
            // Clear the existing items for the player first
            try (PreparedStatement deleteStatement = connection.prepareStatement(
                    "DELETE FROM player_items WHERE player_uuid = ?"
            )) {
                deleteStatement.setString(1, playerUUID.toString());
                deleteStatement.executeUpdate();
            }

            // Insert each ItemStack into the database as a new row
            try (PreparedStatement insertStatement = connection.prepareStatement(
                    "INSERT INTO player_items (player_uuid, item_stack) VALUES (?, ?)"
            )) {
                for (ItemStack itemStack : itemStacks) {
                    String itemStackJson = ItemStackUtils.serializeItemStack(itemStack);
                    insertStatement.setString(1, playerUUID.toString());
                    insertStatement.setString(2, itemStackJson);
                    insertStatement.executeUpdate();
                }
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public List<ItemStack> loadPlayerItems(UUID playerUUID) {
        List<ItemStack> itemStacks = new ArrayList<>();
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "SELECT item_stack FROM player_items WHERE player_uuid = ?"
        )) {
            preparedStatement.setString(1, playerUUID.toString());
            ResultSet resultSet = preparedStatement.executeQuery();

            while (resultSet.next()) {
                String itemStackJson = resultSet.getString("item_stack");
                System.out.println(itemStackJson);
                ItemStack itemStack = ItemStackUtils.deserializeItemStack(itemStackJson);
                if(itemStack == null){
                    continue;
                }
                itemStacks.add(itemStack);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return itemStacks;
    }




}
