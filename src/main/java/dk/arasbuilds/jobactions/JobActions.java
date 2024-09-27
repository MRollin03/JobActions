package dk.arasbuilds.jobactions;

import dk.arasbuilds.jobactions.PluginItems.PlayerOrders;
import dk.arasbuilds.jobactions.commands.OrderCommand;
import dk.arasbuilds.jobactions.commands.OrderTabCompleter;
import dk.arasbuilds.jobactions.database.JobActionsDatabase;
import dk.arasbuilds.jobactions.events.gui.CompletedOrderVaultGUI;
import dk.arasbuilds.jobactions.events.listeners.CompletedOrderVaultGUIListener;
import dk.arasbuilds.jobactions.events.listeners.MarketGUIListener;
import dk.arasbuilds.jobactions.events.listeners.OrderGUIListener;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.plugin.java.JavaPlugin;

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
            debug("Failed to load JobActions's database!" + e.getMessage());
            Bukkit.getPluginManager().disablePlugin(this);
        }

        saveDefaultConfig();
        loadConfig();

        getCommand("order").setExecutor(new OrderCommand());
        getCommand("order").setTabCompleter(new OrderTabCompleter());

        getServer().getPluginManager().registerEvents(new MarketGUIListener(), this);
        getServer().getPluginManager().registerEvents(new OrderGUIListener(), this);
        getServer().getPluginManager().registerEvents(new CompletedOrderVaultGUI(), this);
        getServer().getPluginManager().registerEvents(new CompletedOrderVaultGUIListener(), this);

        loadConfig();

        // Register events
        getServer().getPluginManager().registerEvents(this, this);
    }

    public void loadConfig(){
        reloadConfig();
        config = getConfig();
        printConfig();
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

    public void printConfig(){
        // For debugging purposes, print these values to the console
        if(!isDebug()){return;}
        getLogger().info("Debug activated: " + isDebug());
        getLogger().info("Order Fee: " + getOrderBaseFee());
        getLogger().info("Order Fee Percentage: " + getOrderFeePercentage());
        getLogger().info("Order Limit: " + getOrderLimit());
        getLogger().info("Order Limit Item Count: " + getOrderLimitStackCount());
        getLogger().info("Order Timeout: " + getOrderTimeout());
    }

    public static JobActions getInstance() {
        return instance;
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

    public boolean isDebug(){
        return config.getBoolean("debug");
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

    public void debug(String message){
        if(this.isDebug()){
            this.getLogger().info(message);
        }
    }

    public void help(Player player){
        player.sendMessage("[JobActions] " + ChatColor.GOLD);

        player.sendMessage(ChatColor.WHITE + "» " + ChatColor.DARK_RED + "/order create <item|hand> <amount> <price> - " + ChatColor.GOLD + "Create an order for the given amount of items for a price you'll pay just for creating the order, fees might apply. ");

        player.sendMessage(ChatColor.WHITE + "» " + ChatColor.DARK_RED + "/order vault - " + ChatColor.GOLD + "Opens the vault where you can gather the items of your fulfilled orders.");

        player.sendMessage(ChatColor.WHITE + "» " + ChatColor.DARK_RED + "/order market - " + ChatColor.GOLD + "Open the GUi where you can see and accept orders.");

        player.sendMessage(ChatColor.WHITE + "» " + ChatColor.DARK_RED + "/order cancel <order_ID - " + ChatColor.GOLD + "Cancel the order of the provided ID.");
    }

    public class ColoredAsciiArt {

        private static final JobActions plugin = JobActions.getInstance(); // Get plugin instance for logging

        // ANSI escape codes for color
        public static final String RESET = "\033[0m";  // Reset color
        public static final String RED = "\033[31m";   // Red text
        public static final String GREEN = "\033[32m"; // Green text
        public static final String DARK_GREEN = "\033[33m"; // Dark_Green text
        public static final String YELLOW = "\033[33m"; // Yellow text
        public static final String BLUE = "\033[34m";  // Blue text
        public static final String PURPLE = "\033[35m"; // Purple text
        public static final String CYAN = "\033[36m";  // Cyan text
        public static final String WHITE = "\033[37m"; // White text
        public static final String DARK_GRAY = "\033[90m"; // Dark gray text


        public static void main() {
            // Print colored ASCII art
            System.out.println();
            System.out.println(YELLOW + "       __      __                    " + RESET);
            System.out.println(YELLOW + "      / /___  / /_                   " + RESET);
            System.out.println(YELLOW + " __  / / __ \\/ __ \\                  " + RESET);
            System.out.println(YELLOW + "/ /_/ / /_/ / /_/ /                  " + RESET);
            System.out.println(YELLOW + "\\____/\\____/_.___/ _                 " + RESET);
            System.out.println(RED + "   /   | _____/ /_(_)___  ____  _____" + RESET);
            System.out.println(RED + "  / /| |/ ___/ __/ / __ \\/ __ \\/ ___/" + RESET);
            System.out.println(RED + " / ___ / /__/ /_/ / /_/ / / / (__  ) " + RESET);
            System.out.println(RED + "/_/  |_\\___/\\__/_/\\____/_/ /_/____/  " + RESET);
            System.out.println(DARK_GRAY + "By Aras | " + RESET + DARK_GREEN + "v." + plugin.getDescription().getVersion() + RESET );
        }
    }

}
