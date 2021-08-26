package uk.co.minty_studios.flintixcore.Gui.handler;

import org.bukkit.entity.Player;

public class GuiUtil {

    private Player owner;

    public GuiUtil(Player owner){
        this.owner = owner;
    }

    public Player getOwner(){
        return owner;
    }

    public void setOwner(Player owner){
        this.owner = owner;
    }
}
