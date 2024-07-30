package dk.arasbuilds.jobactions.Utils;

import net.milkbowl.vault.chat.Chat;
import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.economy.EconomyResponse;
import net.milkbowl.vault.permission.Permission;

import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import java.security.Permissions;


public class VaultHook {

    private static Economy econ = null;
    private static Permission perms = null;
    private static Chat chat = null;

    public VaultHook(){

    }

    private static void setupEconomy(){
        RegisteredServiceProvider<Economy> economyProvider = Bukkit.getServer().getServicesManager().getRegistration(Economy.class);

        if(economyProvider != null){
            econ = economyProvider.getProvider();
        }
    }

    private static void setupPermissions(){
        RegisteredServiceProvider<Permission> permissionProvider = Bukkit.getServer().getServicesManager().getRegistration(Permission.class);
        if(permissionProvider != null){
            perms = permissionProvider.getProvider();
        }
    }
    private static void setupChat(){
        RegisteredServiceProvider<Chat> chatProvider = Bukkit.getServer().getServicesManager().getRegistration(Chat.class);
        if(chatProvider != null){
            chat = chatProvider.getProvider();
        }
    }

    private static boolean hasEconomy(){
        return econ != null;
    }

    private static boolean hasPermissions(){
        return perms != null;
    }

    public static String deposit(OfflinePlayer target, int amount){
        if(!hasEconomy()){
            throw new UnsupportedOperationException("The Vault Economy wasn't found!");
        }
        return econ.depositPlayer(target , amount).errorMessage;
    }

    public static String withdraw(OfflinePlayer target, int amount){
        if(!hasEconomy()){
            throw new UnsupportedOperationException("The Vault Economy wasn't found!");
        }
        return econ.withdrawPlayer(target, amount).errorMessage;
    }

    public static double getBalance(OfflinePlayer target){
        if(!hasEconomy()){
            throw new UnsupportedOperationException("The Vault Economy wasn't found!");
        }
        return econ.depositPlayer(target, 0).balance;
    }

    public static String[] getPerms(OfflinePlayer target){
        if(!(hasPermissions())){
            throw new UnsupportedOperationException("The Vault Permissions wasn't found!");
        }
        return perms.getPlayerGroups(target.getPlayer());
    }

    public static String getEconomyCurrency(){
        if(!hasEconomy()){
            throw new UnsupportedOperationException("The Vault Economy wasn't found!");
        }
        return econ.currencyNamePlural();
    }

    static{
        if(Bukkit.getPluginManager().getPlugin("Vault") != null) {
            setupChat();
            setupEconomy();
            setupPermissions();
        }
    }



}
