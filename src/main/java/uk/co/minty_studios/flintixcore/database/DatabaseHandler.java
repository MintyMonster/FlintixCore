package uk.co.minty_studios.flintixcore.database;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;
import org.bukkit.Bukkit;
import uk.co.minty_studios.flintixcore.FlintixCore;
import uk.co.minty_studios.flintixcore.utils.PlayerObject;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

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
                        "STREAK INTEGER NOT NULL, " +
                        "MONEY INTEGER NOT NULL, " +
                        "PLAYTIME INTEGER NOT NULL, " +
                        "FIRSTLOG INTEGER NOT NULL);";

                PreparedStatement prep = con.prepareStatement(sql);
                prep.executeUpdate();
            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public void loadAllPlayers(){ // On eanble after connection
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
                    int money = rs.getInt("MONEY");
                    int playtime = rs.getInt("PLAYTIME");
                    long firstlog = rs.getLong("DATE");

                    playerMap.put(uuid, new PlayerObject(uuid, name, streak, money, playtime, firstlog));
                }

                plugin.getLogger().info("Players loaded!");

            } catch (SQLException throwables) {
                throwables.printStackTrace();
            }
        });
    }

    public void updateDatabase(){
        String sql = "INSERT INTO PLAYERDATA (UUID,NAME,STREAK,MONEY,PLAYTIME,FIRSTLOG) VALUES (?,?,?,?,?,?) ON CONFLICT(UUID) DO UPDATE SET " +
                "STREAK = EXCLUDED.STREAK, MONEY = EXCLUDED.MONEY, PLAYTIME = EXCLUDED.PLAYTIME";

        this.async(() -> {
            try(Connection con = this.getConnection(); PreparedStatement prep = con.prepareStatement(sql)){
                for(Map.Entry<UUID, PlayerObject> entry : playerMap.entrySet()){
                    prep.setString(1, String.valueOf(entry.getKey()));
                    prep.setString(2, entry.getValue().getName());
                    prep.setInt(3, entry.getValue().getStreak());
                    prep.setInt(4, entry.getValue().getMoney());
                    prep.setLong(5, entry.getValue().getPlaytime());
                    prep.setLong(6, entry.getValue().getFirstLogin());
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

        String sql = "INSERT INTO PLAYERDATA (UUID,NAME,STREAK,MONEY,PLAYTIME,FIRSTLOG) VALUES (?,?,?,?,?,?) ON CONFLICT(UUID) DO UPDATE SET " +
                "STREAK = EXCLUDED.STREAK, MONEY = EXCLUDED.MONEY, PLAYTIME = EXCLUDED.PLAYTIME";

        this.async(() -> {
            try(Connection con = this.getConnection(); PreparedStatement prep = con.prepareStatement(sql)){
                for(Map.Entry<UUID, PlayerObject> entry : playerMap.entrySet()){
                    prep.setString(1, String.valueOf(entry.getKey()));
                    prep.setString(2, entry.getValue().getName());
                    prep.setInt(3, entry.getValue().getStreak());
                    prep.setInt(4, entry.getValue().getMoney());
                    prep.setLong(5, entry.getValue().getPlaytime());
                    prep.setLong(6, entry.getValue().getFirstLogin());
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
