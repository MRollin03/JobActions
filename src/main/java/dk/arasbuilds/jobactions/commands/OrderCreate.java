package dk.arasbuilds.jobactions.commands;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import dk.arasbuilds.jobactions.events.gui.CompletedOrderVaultGUI;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class OrderCreate implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {return false;}
        Player player = (Player) commandSender;

        if(args[0].equals("vault")) {
            CompletedOrderVaultGUI.DisplayGUI(player);
            return true;
        }

        if (args.length != 3) {return false;}

        //Price and order amount can't be zero or negative
        if (Integer.parseInt(args[2]) < JobActions.getInstance().getOrderBaseFee() || Integer.parseInt(args[1]) < 1){
            commandSender.sendMessage(ChatColor.RED + "Please enter price larger than the basefee = " + JobActions.getInstance().getOrderBaseFee());
            return true;
        }

        //Get item from hand or requested
        Material material = null;
        if(args[0].equalsIgnoreCase("hand")){
            material = player.getInventory().getItemInMainHand().getType();
        }
        else{
            try {
                material = Material.valueOf(args[0].toUpperCase());
            } catch (IllegalArgumentException e) {
                player.sendMessage("Invalid material");
                return true;
            }
        }

        //Check if item request is above OrderLimitStacks
        if(material.getMaxStackSize() * JobActions.getInstance().getOrderLimitStackCount() < Integer.parseInt(args[1])){
            player.sendMessage("Combined order limit exceeded (max stack size is " + args[1] + ")");
            return true;
        }

        //Create ItemOrder
        ItemOrder order = new ItemOrder(player, material, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        if(order.getOrderID() == null){
            player.sendMessage("Insufficient funds!");
            return true;
        }

        JobActions.getInstance().getJobActionsDatabase().addOrder(order);
        return true;
    }


}
