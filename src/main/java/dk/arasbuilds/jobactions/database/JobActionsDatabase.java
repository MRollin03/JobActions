package dk.arasbuilds.jobactions.database;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import dk.arasbuilds.jobactions.Utils.ItemStackUtils;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.java.JavaPlugin;

import java.sql.*;
import java.time.Clock;
import java.time.LocalTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.UUID;

public class JobActionsDatabase {

    private final Connection connection;
    private JobActions plugin;
    private long TimeInterval;

    public JobActionsDatabase(String path) throws SQLException {
        this.connection = DriverManager.getConnection("jdbc:sqlite:" + path);
        this.plugin = JobActions.getInstance();

        try (Statement statement = connection.createStatement()) {
            statement.execute("""
            CREATE TABLE IF NOT EXISTS players(
                id INTEGER PRIMARY KEY AUTOINCREMENT,
                player_name TEXT,
                player_uuid TEXT UNIQUE
            )
            """);
            statement.execute("""
                CREATE TABLE IF NOT EXISTS vaultstack (
                    id INTEGER PRIMARY KEY AUTOINCREMENT,
                    player_uuid VARCHAR(36),
                    material TEXT,
                    amount INTEGER,
                    itemstack_lore TEXT
                )
            """);
            statement.execute("""
                CREATE TABLE IF NOT EXISTS item_orders (
                    order_id TEXT PRIMARY KEY,
                    player_uuid TEXT NOT NULL,
                    material TEXT NOT NULL,
                    amount INTEGER NOT NULL,
                    price INTEGER NOT NULL,
                    FOREIGN KEY(player_uuid) REFERENCES players(player_uuid)
                )
            """);

            //Get the current time of the server, and places it in the new Column if the given
            // Column in the order_table does not exist. v9.3 -> v9.4.
            Time.valueOf(LocalTime.now(Clock.systemDefaultZone()));
            statement.execute(
                    """
                IF COL_LENGTH(item_orders, creation_time) IS NULL
                BEGIN
                    ALTER TABLE item_orders
                        ADD LONG Time_Existed
                END
                """
            );

            //Starts Timeout Checker loop (Checks the orders Time Existence)
            orderTimeOutChecker();
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    public void orderTimeOutChecker() throws SQLException, InterruptedException {
        while(true){
            if(!connection.isClosed() || !(connection == null)){
                break;
            }
            updateTime();
            TimeInterval = plugin.getTimeInterval(); //Checks here in case config files has been changed and reloaded.
            Thread.sleep(TimeInterval * 60000); //Pauses for Timinterval * 60000 milli = 1 minute
        }
    }


    //< ORDER RELATED METHODS
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
                plugin.debug("Adding order " + orderID);
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }


    //TODO CHECK TIMEOUT FUNCTION
    public void updateTime() throws SQLException {
        ResultSet resultSet = null;
        int value = plugin.getOrderTimeout();
        if(plugin.isTimeout()){
            try(PreparedStatement preparedStatement = connection.prepareStatement(
                    " SELECT order_id,Time_Existed  FROM order_items WHERE Time_Existed >= ? "
            )){
                preparedStatement.setInt(0, value);
                preparedStatement.execute();
                resultSet = preparedStatement.getResultSet();
            }
            catch(SQLException e){
                e.printStackTrace();
                plugin.getLogger().info("Failure getting Time_Existed in SQL-Database.");
            }

            if(resultSet != null){return;}

            while(resultSet.next()){

                Long timeExisted = resultSet.getLong("Time_Exsisted");
                String orderid = resultSet.getString("order_id");

                if(plugin.isTimeout() && plugin.getOrderTimeout() <= timeExisted + (TimeInterval * 60000)){
                    continue;
                }

                try(PreparedStatement preparedStatement = connection.prepareStatement(
                        " INSERT INTO order_items (Time_Existed) WHERE order_id = id VALUES ( ?, ?) "
                )){
                    preparedStatement.setLong(0, (TimeInterval * 60000));
                    preparedStatement.setString(1 ,  orderid);
                    plugin.getLogger().info("Time added to itemorder: " + orderid + "'s Time_Existed");
                }
                catch(SQLException e){
                    e.printStackTrace();
                    plugin.getLogger().info("Failure inserting into Time_Existed in SQL-Databse.");
                }
            }
        }
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
            plugin.debug("Number of orders fetched: " + count);
            plugin.debug("Time taken to fetch orders: " + (endTime - startTime) + " ms");

        } catch (SQLException e) {
            e.printStackTrace();
        }
        return orders;
    }

    // PLAYER VAULT METHODS
    public void addStacksToVault(UUID playerUUID, List<ItemStack> itemStacks) {
        String insertQuery = "INSERT INTO vaultstack (player_uuid, material, amount, itemstack_lore) VALUES (?, ?, ?, ?)";

        try (PreparedStatement preparedStatement = connection.prepareStatement(insertQuery)) {
            for (ItemStack itemStack : itemStacks) {
                preparedStatement.setString(1, playerUUID.toString());
                preparedStatement.setString(2, itemStack.getType().name());
                preparedStatement.setInt(3, itemStack.getAmount());
                preparedStatement.setString(4, itemStack.getItemMeta().getLore() != null ? String.join(",", itemStack.getItemMeta().getLore()) : "");

                preparedStatement.addBatch(); // Add to batch
            }

            preparedStatement.executeBatch(); // Execute all at once
        } catch (SQLException e) {
            e.printStackTrace(); // Handle exception appropriately
        }
    }

    public void clearPlayerVault(UUID playerUUID) {
        try(PreparedStatement deleteStatement = connection.prepareStatement(
                "DELETE from vaultstack WHERE  player_uuid = ?"
        )){
            deleteStatement.setString(1, playerUUID.toString());
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    //TODO add item-meta infomation handling
    public List<ItemStack> getPlayerVault(UUID playerUUID) {
        List<ItemStack> itemStacks = new ArrayList<>();
        try (PreparedStatement getStatement  = connection.prepareStatement(
                "SELECT * from vaultstack WHERE player_uuid =?"
        )){
            getStatement.setString(1, playerUUID.toString());
            ResultSet resultSet = getStatement.executeQuery();

            while(resultSet.next()) {
                Material material = Material.valueOf(resultSet.getString("material"));
                int amount = resultSet.getInt("amount");
                new ItemStack(material, amount);

                itemStacks.add(new ItemStack(material, amount));
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return itemStacks;
    }

}
