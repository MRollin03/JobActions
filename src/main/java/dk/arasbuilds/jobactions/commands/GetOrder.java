package dk.arasbuilds.jobactions.commands;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.PluginItems.PlayerOrders;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;

public class GetOrder implements CommandExecutor {


    @Override
    public boolean onCommand(CommandSender commandSender, Command command, String s, String[] args) {
        if (!(commandSender instanceof Player)) {return false;}
        Player player = (Player) commandSender;

        if(args.length == 0 || args.length > 1) {player.sendMessage("needs arguments");return false;}

        if(args[0].equalsIgnoreCase("all")){
            ArrayList<PlayerOrders> orders = JobActions.getInstance().getJobList().getOrderList();
            for(PlayerOrders order : orders){
                player.sendMessage(order.getOrders().toString());
            }
            return true;

        }

        return false;
    }
}
