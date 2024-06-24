package dk.arasbuilds.jobactions;

import dk.arasbuilds.jobactions.PluginItems.JobList;
import dk.arasbuilds.jobactions.PluginItems.PlayerOrders;
import dk.arasbuilds.jobactions.commands.GetOrder;
import dk.arasbuilds.jobactions.commands.OrderCreate;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public class JobActions extends JavaPlugin implements Listener {
    private static JobActions instance;
    private FileConfiguration config;
    private JobList jobList;
    private PlayerOrders playerOrders;

    @Override
    public void onEnable() {
        instance = this;

        getCommand("ordercreate").setExecutor(new OrderCreate());
        getCommand("getorder").setExecutor(new GetOrder());

        File dataFolder = getDataFolder();
        if (!dataFolder.exists()) {
            dataFolder.mkdirs(); // Create directories if they don't exist
        }
        File dataFile = new File(dataFolder, "orders.dat");
        this.jobList = new JobList(dataFile);
        jobList.loadJobList();

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
        jobList.save();
        instance = null; // Avoid potential memory leak
    }

    public static JobActions getInstance() {
        return instance;
    }

    public FileConfiguration getConfiguration() {
        return config;
    }

    public int getOrderFee() {
        return config.getInt("Order.order-fee");
    }

    public boolean isOrderFeePercentage() {
        return config.getBoolean("Order.order-fee-percentage");
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

    public JobList getJobList() {
        return jobList;
    }

    @EventHandler
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        // Handle player join event if needed
    }
}
