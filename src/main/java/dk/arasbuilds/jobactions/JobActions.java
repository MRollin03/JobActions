package dk.arasbuilds.jobactions;

import dk.arasbuilds.jobactions.PluginItems.PlayerOrders;
import dk.arasbuilds.jobactions.commands.MarketCommand;
import dk.arasbuilds.jobactions.commands.OrderCreate;
import dk.arasbuilds.jobactions.commands.OrderTabCompleter;
import dk.arasbuilds.jobactions.database.JobActionsDatabase;
import dk.arasbuilds.jobactions.events.gui.CompletedOrderVaultGUI;
import dk.arasbuilds.jobactions.events.listeners.CompletedOrderVaultGUIListener;
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

        ColoredAsciiArt.main();

        //initialize SQLLite database
        try{
            if(!getDataFolder().exists()){
                getDataFolder().mkdirs();
            }
            jobActionsDatabase = new JobActionsDatabase(getDataFolder().getAbsolutePath() + "/jobactions.db");
        } catch (SQLException e) {
            e.printStackTrace();
            System.out.println("Failed to load JobActions's database!" + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        getCommand("order").setExecutor(new OrderCreate());
        getCommand("order").setTabCompleter(new OrderTabCompleter());

        getCommand("market").setExecutor(new MarketCommand());

        getServer().getPluginManager().registerEvents(new MarketGUIListener(), this);
        getServer().getPluginManager().registerEvents(new OrderGUIListener(), this);
        getServer().getPluginManager().registerEvents(new CompletedOrderVaultGUI(), this);
        getServer().getPluginManager().registerEvents(new CompletedOrderVaultGUIListener(), this);

        // Save the default config file if it doesn't exist
        saveDefaultConfig();
        config = getConfig();

        // Load config values here.
        int orderbasefee = config.getInt("Order.order-basefee");
        double orderFeePercentage = config.getDouble("Order.order-fee-percentage");
        boolean orderfeepercentageactivated = config.getBoolean("Order.order-fee-percentage-activated");
        int orderLimit = config.getInt("Order.order-limit");
        int orderLimitItemCount = config.getInt("Order.order-limit-stack-count");
        int orderTimeout = config.getInt("Order.order-timeout");

        // For debugging purposes, print these values to the console
        getLogger().info("Order Fee: " + orderbasefee);
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

        // Check if the database is not null before closing the connection
        if (jobActionsDatabase != null) {
            try {
                jobActionsDatabase.closeConnection();
            } catch (SQLException e) {
                e.printStackTrace();
            }
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

    public boolean isOrderBaseFeeActivated() {
        return config.getBoolean("Order.order-base-fee-activated");
    }

    public boolean isOrderFeePercentageActivated(){
        return config.getBoolean("Order.order-fee-percentage-activated");
    }

    public int getOrderLimit() {
        return config.getInt("Order.order-limit");
    }

    public int getOrderLimitStackCount() {
        return config.getInt("Order.order-limit-stack-count");
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

    public class ColoredAsciiArt {
        // ANSI escape codes for color
        public static final String RESET = "\033[0m";  // Reset color
        public static final String RED = "\033[31m";   // Red text
        public static final String GREEN = "\033[32m"; // Green text
        public static final String YELLOW = "\033[33m"; // Yellow text
        public static final String BLUE = "\033[34m";  // Blue text
        public static final String PURPLE = "\033[35m"; // Purple text
        public static final String CYAN = "\033[36m";  // Cyan text
        public static final String WHITE = "\033[37m"; // White text

        public static void main() {
            // Print colored ASCII art
            System.out.println(RED + "       __      __                    " + RESET);
            System.out.println(GREEN + "      / /___  / /_                   " + RESET);
            System.out.println(YELLOW + " __  / / __ \\/ __ \\                  " + RESET);
            System.out.println(BLUE + "/ /_/ / /_/ / /_/ /                  " + RESET);
            System.out.println(PURPLE + "\\____/\\____/_.___/ _                 " + RESET);
            System.out.println(CYAN + "   /   | _____/ /_(_)___  ____  _____" + RESET);
            System.out.println(WHITE + "  / /| |/ ___/ __/ / __ \\/ __ \\/ ___/" + RESET);
            System.out.println(RED + " / ___ / /__/ /_/ / /_/ / / / (__  ) " + RESET);
            System.out.println(GREEN + "/_/  |_\\___/\\__/_/\\____/_/ /_/____/  " + RESET);
        }
    }

}
