package uk.co.minty_studios.flintixcore.utils;

import java.util.Date;
import java.util.UUID;

public class PlayerObject {

    private final UUID uuid;
    private final String name;
    private int streak;
    private int money;
    private long playtime;
    private long firstLogin;

    public PlayerObject(UUID uuid, String name, int streak, int money, long playtime, long firstLogin){
        this.uuid = uuid;
        this.name = name;
        this.streak = streak;
        this.money = money;
        this.playtime = playtime;
        this.firstLogin = firstLogin;
    }

    public void updatePlayer(int streak, int money, long playtime, long firstlog){
        this.streak = streak;
        this.money = money;
        this.playtime = playtime;
        this.firstLogin = firstlog;
    }

    public UUID getUuid() { return uuid; }

    public String getName(){
        return name;
    }

    public int getStreak(){
        return streak;
    }

    public int getMoney(){
        return money;
    }

    public long getPlaytime(){
        return playtime;
    }

    public long getFirstLogin(){
        return firstLogin;
    }

    public void setStreak(int streak){
        this.streak = streak;
    }

    public void setMoney(int money){
        this.money = money;
    }

    public void setPlaytime(long playtime){
        this.playtime = playtime;
    }

    public void getFirstLog(long firstLogin){
        this.firstLogin = firstLogin;
    }
}
