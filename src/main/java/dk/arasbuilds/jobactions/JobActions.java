package dk.arasbuilds.jobactions;

import dk.arasbuilds.jobactions.PluginItems.PlayerOrders;
import dk.arasbuilds.jobactions.commands.MarketCommand;
import dk.arasbuilds.jobactions.commands.OrderCreate;
import dk.arasbuilds.jobactions.database.JobActionsDatabase;
import dk.arasbuilds.jobactions.events.gui.CompletedOrderVaultGUI;
import dk.arasbuilds.jobactions.events.listeners.MarketGUIListener;
import dk.arasbuilds.jobactions.events.listeners.OrderGUIListener;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;
import java.sql.SQLException;

public class JobActions extends JavaPlugin implements Listener {
    private static JobActions instance;
    private FileConfiguration config;
    private PlayerOrders playerOrders;
    private JobActionsDatabase jobActionsDatabase;


    @Override
    public void onEnable() {
        instance = this;

        //initialize SQLLite database
        try{
            if(getDataFolder().exists()){
                getDataFolder().mkdirs();
            }
            jobActionsDatabase = new JobActionsDatabase(getDataFolder().getAbsolutePath() + "/jobactions.db");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load JobActions's database!" + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        getCommand("ordercreate").setExecutor(new OrderCreate());
        getCommand("market").setExecutor(new MarketCommand());

        getServer().getPluginManager().registerEvents(new MarketGUIListener(), this);
        getServer().getPluginManager().registerEvents(new OrderGUIListener(), this);
        Bukkit.getPluginManager().registerEvents(new CompletedOrderVaultGUI(), this);


        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs(); // Create directories if they don't exist
        }
        File dataFile = new File(dataFolder, "orders.dat");

        // Save the default config file if it doesn't exist
        saveDefaultConfig();
        config = getConfig();

        // Load config values here.
        int orderFee = config.getInt("Order.order-fee");
        boolean orderFeePercentage = config.getBoolean("Order.order-fee-percentage");
        int orderLimit = config.getInt("Order.order-limit");
        int orderLimitItemCount = config.getInt("Order.order-limit-item-count");
        int orderTimeout = config.getInt("Order.order-timeout");

        // For debugging purposes, print these values to the console
        getLogger().info("Order Fee: " + orderFee);
        getLogger().info("Order Fee Percentage: " + orderFeePercentage);
        getLogger().info("Order Limit: " + orderLimit);
        getLogger().info("Order Limit Item Count: " + orderLimitItemCount);
        getLogger().info("Order Timeout: " + orderTimeout);

        // Register events
        getServer().getPluginManager().registerEvents(this, this);
    }

    @Override
    public void onDisable() {
        instance = null; // Avoid potential memory leak
        try{
            jobActionsDatabase.closeConnection();
        }
        catch(SQLException e){
            e.printStackTrace();
        }
    }

    public static JobActions getInstance() {
        return instance;
    }

    public FileConfiguration getConfiguration() {
        return config;
    }

    public int getOrderBaseFee() {
        return config.getInt("Order.order-basefee");
    }
    public double getOrderFeePercentage() {
        return config.getDouble("Order.order-fee-percentage");
    }

    public boolean isOrderFeePercentage() {
        return config.getBoolean("Order.order-fee-percentage-activated");
    }

    public int getOrderLimit() {
        return config.getInt("Order.order-limit");
    }

    public int getOrderLimitStackCount() {
        return config.getInt("Order.order-limit-item-count");
    }

    public int getOrderTimeout() {
        return config.getInt("Order.order-timeout");
    }

    public JobActionsDatabase getJobActionsDatabase() {
        return jobActionsDatabase;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Handle player join event if needed
    }
}
