package uk.co.minty_studios.flintixcore;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import uk.co.minty_studios.flintixcore.commands.CommandManager;
import uk.co.minty_studios.flintixcore.database.DatabaseHandler;
import uk.co.minty_studios.flintixcore.listeners.GuiClickListener;
import uk.co.minty_studios.flintixcore.listeners.PlayerJoinListener;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlintixCore extends JavaPlugin {

    public final String flintixRed = "&#FF5776#";
    public final String flintixGreen = "&#61C9A8#";
    public final String flintixBlue = "&#0775A5#";
    public final String flintixBeige = "&#FFE3C4#";
    public final String flintixOrange = "&#FF9C57#";
    public final String flintixYellow = "&#FFC457#";
    public final String flintixBlack = "&#15161A#";
    public final String flintixMidGrey = "#&888888#";
    public final String flintixGrey = "&#EEEEEE#";


    private CommandManager commandManager;
    private DatabaseHandler databaseHandler;

    @Override
    public void onEnable(){

        this.saveDefaultConfig();

        // Classes
        getLogger().info("Preparing to enable...");
        getLogger().info("Loading classes....");
        this.commandManager = new CommandManager(this);
        this.databaseHandler = new DatabaseHandler(this);

        getLogger().info("Classes loaded...");

        // Listeners
        getLogger().info("Registering events....");
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new GuiClickListener(), this);
        pluginManager.registerEvents(new PlayerJoinListener(), this);

        // Config stuff

        // Database stuff
        getLogger().info("Checking databases...");
        databaseHandler.createPlayerDatabase();
        getLogger().info("Checked databases! We are okay!");

        new BukkitRunnable(){
            @Override
            public void run(){
                getLogger().info("Loading data...");
                databaseHandler.loadAllPlayers();
                getLogger().info("Loaded data!");
            }
        }.runTaskLater(this, 100);

        // PlaceholderAPI?

    }

    @Override
    public void onDisable() {
        // Disable events
        this.reloadConfig();
    }

    private String translateHex(String message){
        final Pattern hexPattern = Pattern.compile("&#" + "([A-Fa-f0-9]{6})" + "#");
        Matcher matcher = hexPattern.matcher(message);
        StringBuffer buffer = new StringBuffer(message.length() + 4 * 8);

        while(matcher.find()){
            String group = matcher.group(1);
            matcher.appendReplacement(buffer, ChatColor.COLOR_CHAR + "x"
                + ChatColor.COLOR_CHAR + group.charAt(0) + ChatColor.COLOR_CHAR + group.charAt(1)
                + ChatColor.COLOR_CHAR + group.charAt(2) + ChatColor.COLOR_CHAR + group.charAt(3)
                + ChatColor.COLOR_CHAR + group.charAt(4) + ChatColor.COLOR_CHAR + group.charAt(5)
            );
        }

        return matcher.appendTail(buffer).toString();
    }

    public void sendHex(Player player, String message){
        player.sendMessage(ChatColor.translateAlternateColorCodes('&', this.translateHex(message)));
    }
}
