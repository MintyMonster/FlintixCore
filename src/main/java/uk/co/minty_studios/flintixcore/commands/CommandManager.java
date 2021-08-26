package uk.co.minty_studios.flintixcore.commands;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.command.TabExecutor;
import org.bukkit.entity.Player;
import uk.co.minty_studios.flintixcore.FlintixCore;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class CommandManager implements TabExecutor {

    private final Map<String, MasterCommand> commands = new HashMap<>();
    private final FlintixCore plugin;

    public CommandManager(FlintixCore plugin) {
        this.plugin = plugin;

        plugin.getCommand("flintixcore").setExecutor(this);

        addCommands("flintixcore"); // Add the commands here after a comma
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args){
        if(args.length > 0){
            MasterCommand master = commands.get(command.getName().toLowerCase());
            if(master != null) {
                for(ChildCommand child : master.getChildCommands().values()){
                    if(args[0].equalsIgnoreCase(child.getName())){
                        if(sender instanceof ConsoleCommandSender){
                            if(child.consoleUse()){
                                child.perform(sender, args);
                            }else {
                                Bukkit.getConsoleSender().sendMessage("This command cannot be used from console!");
                            }
                        } else if(sender instanceof Player){
                            if(sender.hasPermission(child.getPermission())){
                                child.perform(sender, args);
                            }
                        }
                    }
                }
            }
        } else {
            if(sender instanceof ConsoleCommandSender) return true;
            Player player = (Player) sender;
            MasterCommand master = commands.get(command.getName().toLowerCase());
            if(master != null) {
                plugin.sendHex(player, plugin.flintixRed + "FlintixCore " + plugin.flintixMidGrey + "-> " + plugin.flintixGreen + "Made by MintyMonster");
                master.getChildCommands().values().forEach(c -> {
                    if (player.hasPermission(c.getPermission()) || player.isOp()){
                        plugin.sendHex(player, plugin.flintixYellow + c.getSyntax() + plugin.flintixRed + ": " + plugin.flintixMidGrey + c.getDescription());
                    }
                });
            }
        }

        return true;
    }

    public void addCommand(String command, ChildCommand child){
        commands.computeIfAbsent(command.toLowerCase(), c -> new MasterCommand()).addCommand(child);
    }

    public void addCommands(String command, ChildCommand... children){
        MasterCommand master = commands.computeIfAbsent(command.toLowerCase(), c -> new MasterCommand());
        for(ChildCommand child : children)
            master.addCommand(child);
    }

    @Override
    public List<String> onTabComplete(CommandSender sender, Command command, String label, String[] args){
        MasterCommand master = commands.get(command.getName().toLowerCase());
        if(master != null){
            if(args.length == 1){
                List<String> children = new ArrayList<>();
                for(ChildCommand child : master.getChildCommands().values()){
                    children.add(child.getName());
                }

                return children;
            } else{
                for(ChildCommand child : master.getChildCommands().values()){
                    if(args[0].equalsIgnoreCase(child.getName())){
                        return child.onTab(sender, args);
                    }
                }
            }
        }

        return null;
    }
}
