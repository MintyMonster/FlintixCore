package uk.co.minty_studios.flintixcore.utils;

import java.util.Date;
import java.util.UUID;

public class PlayerObject {

    private final UUID uuid;
    private final String name;
    private int streak;
    private long firstLogin;
    private long money;
    private long lastLog;

    public PlayerObject(UUID uuid, String name, int streak, long firstLogin, long money, long lastLog){
        this.uuid = uuid;
        this.name = name;
        this.streak = streak;
        this.firstLogin = firstLogin;
        this.money = money;
        this.lastLog = lastLog;
    }

    public void updatePlayer(int streak, long firstlog, long money, long lastLog){
        this.streak = streak;
        this.firstLogin = firstlog;
        this.money = money;
        this.lastLog = lastLog;
    }

    public UUID getUuid() { return uuid; }

    public String getName(){
        return name;
    }

    public int getStreak(){
        return streak;
    }

    public long getFirstLogin(){
        return firstLogin;
    }

    public long getMoney(){
        return money;
    }

    public void setStreak(int streak){
        this.streak = streak;
    }

    public void setMoney(int money){
        this.money = money;
    }

    public void getFirstLog(long firstLogin){
        this.firstLogin = firstLogin;
    }

    public void setLastLog(long lastLog){
        this.lastLog = lastLog;
    }

    public long getLastLog(){
        return lastLog;
    }
}
