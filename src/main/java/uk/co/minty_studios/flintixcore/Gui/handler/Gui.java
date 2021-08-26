package uk.co.minty_studios.flintixcore.Gui.handler;

import org.bukkit.Bukkit;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.InventoryHolder;

public abstract class Gui implements InventoryHolder {

    protected Inventory inventory;
    protected GuiUtil guiUtil;

    public Gui(GuiUtil guiUtil){
        this.guiUtil = guiUtil;
    }

    public abstract String getMenuName();

    public abstract int getSlots();

    public abstract void handleMenu(InventoryClickEvent e);

    public abstract void setItems();

    public void open(){
        inventory = Bukkit.createInventory(this, getSlots(), getMenuName());

        this.setItems();

        guiUtil.getOwner().openInventory(inventory);
    }

    @Override
    public Inventory getInventory(){
        return inventory;
    }
}
