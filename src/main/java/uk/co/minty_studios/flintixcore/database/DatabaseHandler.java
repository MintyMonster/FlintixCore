package uk.co.minty_studios.flintixcore.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import org.bukkit.Statistic;
import org.bukkit.entity.Player;
import uk.co.minty_studios.flintixcore.FlintixCore;
import uk.co.minty_studios.flintixcore.utils.PlayerObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.time.Duration;
import java.time.Instant;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.*;

public class DatabaseHandler {

    private static final Map<UUID, PlayerObject> playerMap = new HashMap<>();

    private final FlintixCore plugin;
    private HikariConfig config = new HikariConfig();
    private HikariDataSource hikari;

    public DatabaseHandler(FlintixCore plugin) {
        this.plugin = plugin;

        String hostname = plugin.getConfig().getString("settings.database.hostname");
        int port = plugin.getConfig().getInt("settings.database.port");
        String dbName = plugin.getConfig().getString("settings.database.db-name");
        boolean ssl = plugin.getConfig().getBoolean("settings.database.ssl");
        String username = plugin.getConfig().getString("settings.database.username");
        String password = plugin.getConfig().getString("settings.database.password");

        config.setJdbcUrl("jdbc:mysql://" + hostname + ":" + port + "/" + dbName + "?useSSL=" + ssl);
        config.setUsername(username);
        config.setPassword(password);
        config.setMaximumPoolSize(5);

        try {
            this.hikari = new HikariDataSource(config);
        }catch (Exception ex){
            ex.printStackTrace();
        }
    }

    public Map<UUID, PlayerObject> getPlayerMap(){
        return playerMap;
    }

    public void newPlayer(Player player){
        if(!playerExists(player.getUniqueId())){
            playerMap.put(player.getUniqueId(), new PlayerObject(player.getUniqueId(), player.getDisplayName(), 1, Instant.now().getEpochSecond(), 0, Instant.now().getEpochSecond()));
        }else{
            SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
            long lastLog = playerMap.get(player.getUniqueId()).getLastLog();
            long now = Instant.now().getEpochSecond();

            if(!(sdf.format(lastLog).equals(sdf.format(now)))
                    && (LocalDate.ofEpochDay(now).equals(LocalDate.ofEpochDay(lastLog).plusDays(1)))){

                playerMap.get(player.getUniqueId()).setStreak(playerMap.get(player.getUniqueId()).getStreak() + 1);
                playerMap.get(player.getUniqueId()).setLastLog(Instant.now().getEpochSecond());

                if(plugin.getConfig().getBoolean("messages.streaks.send-streak-message"))
                    player.sendMessage(plugin.parsePlaceholders(plugin.getConfig().getString("messages.streaks.streak-added"))
                            .replace("%streak%", String.valueOf(playerMap.get(player.getUniqueId()).getStreak())));

            }else if((ChronoUnit.HOURS.between(LocalDate.ofEpochDay(lastLog), LocalDate.ofEpochDay(now)) > 48)){

                playerMap.get(player.getUniqueId()).setStreak(0);

                if(plugin.getConfig().getBoolean("messages.streaks.send-streak-message"))
                    player.sendMessage(plugin.parsePlaceholders(plugin.getConfig().getString("messages.streaks.streak-broken")));
            }
        }
    }

    private Boolean playerExists(UUID uuid){
        if(playerMap.containsKey(uuid))
            return true;

        return false;
    }

    private Connection getConnection(){
        Connection con = null;
        try{
            con = hikari.getConnection();
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }

        return con;
    }

    public void async(Runnable runnable){
        Bukkit.getScheduler().runTaskAsynchronously(this.plugin, runnable);
    }

    public void sync(Runnable runnable){
        Bukkit.getScheduler().runTask(this.plugin, runnable);
    }

    public void createPlayerDatabase(){
        this.async(() -> {
            try(Connection con = this.getConnection()){
                String sql = "CREATE TABLE IF NOT EXISTS PLAYERDATA (" +
                        "UUID TEXT PRIMARY KEY NOT NULL, " +
                        "NAME TEXT NOT NULL, " +
                        "MONEY INTEGER NOT NULL, " +
                        "STREAK INTEGER NOT NULL, " +
                        "FIRSTLOG INTEGER NOT NULL," +
                        "LASTLOG INTEGER NOT NULL);";

                PreparedStatement prep = con.prepareStatement(sql);
                prep.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public void loadAllPlayers(){ // On enable after connection
        String sql = "SELECT * FROM PLAYERDATA";

        this.sync(() -> {
            try(Connection con = this.getConnection(); PreparedStatement prep = con.prepareStatement(sql)){

                ResultSet rs = prep.executeQuery();
                while(rs.next()){

                    UUID uuid = UUID.fromString(rs.getString("UUID"));

                    String name = plugin.getServer().getPlayer(uuid) != null
                            ? plugin.getServer().getPlayer(uuid).getName()
                            : plugin.getServer().getOfflinePlayer(uuid).getName();

                    int streak = rs.getInt("STREAK");
                    long firstlog = rs.getLong("FIRSTLOG");
                    long money = rs.getLong("MONEY");
                    long lastLog = rs.getLong("LASTLOG");

                    playerMap.put(uuid, new PlayerObject(uuid, name, streak, firstlog, money, lastLog));
                }

                plugin.getLogger().info("Players loaded!");

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public void updateDatabase(){
        String sql = "INSERT INTO PLAYERDATA (UUID,NAME,MONEY,STREAK,FIRSTLOG,LASTLOG) VALUES (?,?,?,?,?,?,?) ON CONFLICT(UUID) DO UPDATE SET " +
                "MONEY = EXCLUDED.MONEY, STREAK = EXCLUDED.STREAK, LASTLOG = EXCLUDED.LASTLOG";

        this.async(() -> {
            try(Connection con = this.getConnection(); PreparedStatement prep = con.prepareStatement(sql)){
                for(Map.Entry<UUID, PlayerObject> entry : playerMap.entrySet()){
                    prep.setString(1, String.valueOf(entry.getKey()));
                    prep.setString(2, entry.getValue().getName());
                    prep.setLong(3, entry.getValue().getMoney());
                    prep.setInt(4, entry.getValue().getStreak());
                    prep.setLong(5, entry.getValue().getFirstLogin());
                    prep.setLong(6, entry.getValue().getLastLog());
                    prep.addBatch();
                }

                prep.executeBatch();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public void savePlayers(){
        plugin.getLogger().info("Saving player data... This may take a moment.");

        String sql = "INSERT INTO PLAYERDATA (UUID,NAME,MONEY,STREAK,FIRSTLOG,LASTLOG) VALUES (?,?,?,?,?,?,?) ON CONFLICT(UUID) DO UPDATE SET " +
                "MONEY = EXCLUDED.MONEY, STREAK = EXCLUDED.STREAK, LASTLOG = EXCLUDED.LASTLOG";

        this.async(() -> {
            try(Connection con = this.getConnection(); PreparedStatement prep = con.prepareStatement(sql)){
                for(Map.Entry<UUID, PlayerObject> entry : playerMap.entrySet()){
                    prep.setString(1, String.valueOf(entry.getKey()));
                    prep.setString(2, entry.getValue().getName());
                    prep.setLong(3, entry.getValue().getMoney());
                    prep.setInt(4, entry.getValue().getStreak());
                    prep.setLong(5, entry.getValue().getFirstLogin());
                    prep.setLong(6, entry.getValue().getLastLog());
                    prep.addBatch();
                }

                prep.executeBatch();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }

            plugin.getLogger().info("Player data saved!");
        });
    }
}
