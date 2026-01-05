package dk.arasbuilds.jobactions.database;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

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
                     time_of_creation INTEGER NOT NULL,
                     FOREIGN KEY(player_uuid) REFERENCES players(player_uuid)
                 )
            """);
        }
    }

    public void closeConnection() throws SQLException {
        if (connection != null && !connection.isClosed()) {
            connection.close();
        }
    }

    // SCHEDULE TASK
    public void startOrderTimeoutChecker() {
        long intervalMinutes = plugin.getTimeInterval();
        if (intervalMinutes <= 0) {
            plugin.debug("Invalid time interval: " + intervalMinutes + " minutes. Scheduler not started.");
            return;
        }

        long ticks = intervalMinutes * 60 * 20L;
        plugin.debug("Starting async order timeout checker with interval: " + intervalMinutes + " min (" + ticks + " ticks)");

        Bukkit.getScheduler().runTaskTimerAsynchronously(plugin, () -> {
            plugin.debug("Running checkOrderTimeouts()");
            checkOrderTimeouts();
        }, ticks, ticks);
    }



    // FIXED: Async DB query, then sync removal with proper vault return
    private void checkOrderTimeouts() {
        if (!plugin.isTimeout()) {
            return;
        }

        long timeoutMs = TimeUnit.MINUTES.toMillis(plugin.getOrderTimeout());
        long now = System.currentTimeMillis();

        plugin.debug("Checking time for item orders...");

        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT order_id, player_uuid, material, amount, price, time_of_creation FROM item_orders WHERE ? - time_of_creation >= ?"
        )) {
            ps.setLong(1, now);
            ps.setLong(2, timeoutMs);
            ResultSet rs = ps.executeQuery();

            List<ItemOrder> expiredOrders = new ArrayList<>();
            while (rs.next()) {
                String orderID = rs.getString("order_id");
                UUID playerUUID = UUID.fromString(rs.getString("player_uuid"));
                Material material = Material.valueOf(rs.getString("material"));
                int amount = rs.getInt("amount");
                int price = rs.getInt("price");
                long creationTime = rs.getLong("time_of_creation");

                // Calculate how long the order has existed
                long ageMinutes = TimeUnit.MILLISECONDS.toMinutes(now - creationTime);

                OfflinePlayer player = Bukkit.getOfflinePlayer(playerUUID);
                ItemOrder order = new ItemOrder(player, material, amount, price, orderID);
                expiredOrders.add(order);

                plugin.debug("Order " + orderID + " expired (age: " + ageMinutes + " minutes)");
            }

            // Schedule removal on MAIN THREAD with proper item return to vault
            if (!expiredOrders.isEmpty()) {
                Bukkit.getScheduler().runTask(plugin, () -> {
                    for (ItemOrder order : expiredOrders) {
                        if (removeItemOrder(order)) {
                            // Return items to player's vault
                            UUID playerUUID = order.getUuid();
                            ItemStack itemStack = new ItemStack(order.getMaterial(), order.getAmount());
                            addStacksToVault(playerUUID, List.of(itemStack));

                            plugin.debug("Order " + order.getOrderID() + " timed out and removed. Items returned to vault.");

                            // Notify player if online
                            Player player = Bukkit.getPlayer(playerUUID);
                            if (player != null && player.isOnline()) {
                                player.sendMessage("§cYour order for " + order.getAmount() + "x " +
                                        order.getMaterial().name() + " has expired and items were returned to your vault.");
                            }
                        }
                    }
                });
            }

        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to check time for item orders: " + e.getMessage());
            e.printStackTrace();
        }
    }


    // HELPER METHOD: Get order age in minutes
    public long getOrderAgeMinutes(String orderID) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT time_of_creation FROM item_orders WHERE order_id = ?"
        )) {
            ps.setString(1, orderID);
            ResultSet rs = ps.executeQuery();

            if (rs.next()) {
                long creationTime = rs.getLong("time_of_creation");
                long now = System.currentTimeMillis();
                return TimeUnit.MILLISECONDS.toMinutes(now - creationTime);
            }
        } catch (SQLException e) {
            plugin.getLogger().severe("Failed to get order age: " + e.getMessage());
        }
        return -1;
    }



    //< ORDER RELATED METHODS
    public void addPlayer(Player player) {
        try (PreparedStatement preparedStatement = connection.prepareStatement(
                "INSERT INTO players (player_uuid, player_name) VALUES (?, ?)"
        )) {
            preparedStatement.setString(1, player.getUniqueId().toString());
            preparedStatement.setString(2, player.getName());
            preparedStatement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public boolean playerExists(UUID uuid) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT 1 FROM players WHERE player_uuid = ?"
        )) {
            ps.setString(1, uuid.toString());
            ResultSet resultSet = ps.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    public void addOrder(ItemOrder order) { //TODO ADD TIME_OF_CREATION
        try {
            // Check if the order already exists
            if (orderExists(order.getOrderID())) {
                // Update the existing order if necessary
                updateOrder(order);
            } else {
                // Insert new order
                try (PreparedStatement ps = connection.prepareStatement(
                        "INSERT INTO item_orders (order_id, player_uuid, material, amount, price, time_of_creation) VALUES (?, ?, ?, ?, ?, ?)"
                )) {
                    ps.setString(1, order.getOrderID());
                    ps.setString(2, order.getUuid().toString());
                    ps.setString(3, order.getMaterial().name());
                    ps.setInt(4, order.getAmount());
                    ps.setInt(5, order.getPrice());
                    ps.setLong(6, System.currentTimeMillis());
                    ps.executeUpdate();
                }

            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private boolean orderExists(String orderID) {
        try (PreparedStatement ps = connection.prepareStatement(
                "SELECT 1 FROM item_orders WHERE order_id = ?"
        )) {
            ps.setString(1, orderID);
            ResultSet resultSet = ps.executeQuery();
            return resultSet.next();
        } catch (SQLException e) {
            e.printStackTrace();
            return false;
        }
    }

    private void updateOrder(ItemOrder order) { //TODO (MAYBE) ORDER UPDATE
        try (PreparedStatement ps = connection.prepareStatement(
                "UPDATE item_orders SET player_uuid = ?, material = ?, amount = ?, price = ? WHERE order_id = ?"
        )) {
            ps.setString(1, order.getUuid().toString());
            ps.setString(2, order.getMaterial().toString());
            ps.setInt(3, order.getAmount());
            ps.setInt(4, order.getPrice());
            ps.setString(5, order.getOrderID());
            ps.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public ArrayList<ItemOrder> getOrdersByPlayer(UUID playerUUID) {
        ArrayList<ItemOrder> orders = new ArrayList<>();
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
