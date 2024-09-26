package dk.arasbuilds.jobactions.commands;

import dk.arasbuilds.jobactions.JobActions;
import dk.arasbuilds.jobactions.PluginItems.ItemOrder;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;

import java.util.ArrayList;
import java.util.List;

public class OrderTabCompleter implements TabCompleter {
    @Override
    public List<String> onTabComplete(CommandSender commandSender, Command command, String s, String[] strings) {

        if(strings.length == 1) {
            List<String> list = new ArrayList<>();

            // Sub arguments under /order
            list.add("create");
            // list.add("list");
            list.add("cancel");
            return list;
        }
        if(strings.length == 2 && strings[0].equals("create")) {
            List<String> list = new ArrayList<>();

            // hand or any material
            list.add("hand");
            for (Material material : Material.values()) {
                //limits the items to current written statement
                if(material.name().toLowerCase().startsWith(strings[1])) {
                    list.add(material.name().toLowerCase());
                }
            }
            return list;
        }
        if(strings.length == 2 && strings[0].equals("cancel")) {
            List<String> list = new ArrayList<>();

            ArrayList<ItemOrder> orderlist = JobActions.getInstance().getJobActionsDatabase().getAllItemOrders();
            for (ItemOrder order : orderlist) {
                //limits the items to current written statement
                if(order.getOrderID().toLowerCase().startsWith(strings[1])) {
                    list.add(order.getOrderID());
                }
            }

            return list;
        }


        return null;
    }
}
