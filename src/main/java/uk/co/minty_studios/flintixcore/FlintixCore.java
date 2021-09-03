package uk.co.minty_studios.flintixcore;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;
import uk.co.minty_studios.flintixcore.Gui.handler.GuiUtil;
import uk.co.minty_studios.flintixcore.commands.CommandManager;
import uk.co.minty_studios.flintixcore.database.DatabaseHandler;
import uk.co.minty_studios.flintixcore.listeners.GuiClickListener;
import uk.co.minty_studios.flintixcore.utils.DatabaseUpdater;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class FlintixCore extends JavaPlugin {

    private static final Map<Player, GuiUtil> playerMap = new HashMap<>();

    public final String flintixRed = "&#FF5776#";
    public final String flintixGreen = "&#61C9A8#";
    public final String flintixBlue = "&#0775A5#";
    public final String flintixBeige = "&#FFE3C4#";
    public final String flintixOrange = "&#FF9C57#";
    public final String flintixYellow = "&#FFC457#";
    public final String flintixBlack = "&#15161A#";
    public final String flintixMidGrey = "#&888888#";
    public final String flintixGrey = "&#EEEEEE#";
    public final String flintixDiscord = "&#5865F2#";
    public final String flintixTwitter = "&#1DA1F2#";
    public final String flintixYoutube = "&#FF0000#";
    public final String flintixTikTok = "&#00f2EA#";
    public final String flintixWebsite = "&#FF5776#";


    private CommandManager commandManager;
    private DatabaseHandler databaseHandler;
    private DatabaseUpdater databaseUpdater;

    @Override
    public void onEnable(){

        this.saveDefaultConfig();

        // Classes
        getLogger().info("Preparing to enable...");
        getLogger().info("Loading classes....");
        this.commandManager = new CommandManager(this);
        this.databaseHandler = new DatabaseHandler(this);
        this.databaseUpdater = new DatabaseUpdater(databaseHandler,this);

        getLogger().info("Classes loaded...");

        // Listeners
        getLogger().info("Registering events....");
        PluginManager pluginManager = getServer().getPluginManager();

        pluginManager.registerEvents(new GuiClickListener(), this);

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


        this.getLogger().info("Initialising Player saving...");
        this.databaseUpdater.updateDatabaseTimer();
        this.getLogger().info("Initialised Player saving!");

    }

    @Override
    public void onDisable() {
        // Disable events
        this.databaseHandler.savePlayers();
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

    public String returnHex(String message){
        return ChatColor.translateAlternateColorCodes('&', this.translateHex(message));
    }

    public String parsePlaceholders(String message){
        return message.replace("%flintix_red%", this.flintixRed)
                .replace("%flintix_green%", this.flintixGreen)
                .replace("%flintix_blue%", this.flintixBlue)
                .replace("%flintix_beige%", this.flintixBeige).
                replace("%flintix_orange%", this.flintixOrange)
                .replace("%flintix_yellow%", this.flintixYellow)
                .replace("%flintix_black%", this.flintixBlack)
                .replace("%flintix_mid_grey%", this.flintixMidGrey)
                .replace("%flintix_grey%", this.flintixGrey)
                .replace("%flintix_discord%", this.flintixDiscord)
                .replace("%flintix_twitter%", this.flintixTwitter)
                .replace("%flintix_youtube%", this.flintixYoutube)
                .replace("%flintix_tiktok%", this.flintixTikTok)
                .replace("%flintix_website%", this.flintixWebsite);
    }

    public String getTime(long time){
        int seconds = (int) (time / 20);
        long sec = seconds % 60;
        long minutes = seconds % 3600 / 60;
        long hours = seconds % 86400 / 3600;
        long days = seconds / 86400;

        return days + " days, " + hours + " hour(s), " + minutes + " minute(s), and " + seconds + " second(s).";
    }

    // Gui util
    public GuiUtil getMenuUtil(Player p){
        GuiUtil util;

        if(playerMap.containsKey(p))
            return playerMap.get(p);
        else{
            util = new GuiUtil(p);
            playerMap.put(p, util);

            return util;
        }
    }
}
