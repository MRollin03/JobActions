package dk.arasbuilds.jobactions.commands;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import dk.arasbuilds.jobactions.PluginItems.JobList;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.Objects;

public class OrderCreate implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String label, String[] args) {
        if (!(commandSender instanceof Player)) {return false;}
        if (args.length != 3) {return false;}
        Player player = (Player) commandSender;

        System.out.print(1);

        Material material = null;
        if(args[0].equalsIgnoreCase("hand")){
            System.out.println("HAND!");
            System.out.println(2);
            material = player.getInventory().getItemInMainHand().getType();
            System.out.println(material);
            System.out.println("hand " + 3);
        }
        else{
            try {
                material = Material.valueOf(args[0].toLowerCase());
                System.out.println("item " + 3);
            } catch (IllegalArgumentException e) {
                player.sendMessage("Invalid material");
                return false;
            }
        }

        if(material.getMaxStackSize() * JobActions.getInstance().getOrderLimitStackCount() < Integer.parseInt(args[1])){
            player.sendMessage("Combined order limit exceeded (max stack size is " + args[1] + ")");

            System.out.println(5);
            return false;
        }

        System.out.println(6);
        ItemOrder order = new ItemOrder(player, material, Integer.parseInt(args[1]), Integer.parseInt(args[2]));
        System.out.println(7);
        return JobActions.getInstance().getJobList().add(order, player);


    }


}
