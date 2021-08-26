package uk.co.minty_studios.flintixcore.Gui.rewards;

import org.bukkit.event.inventory.InventoryClickEvent;
import uk.co.minty_studios.flintixcore.FlintixCore;
import uk.co.minty_studios.flintixcore.Gui.handler.Gui;
import uk.co.minty_studios.flintixcore.Gui.handler.GuiUtil;

public class RewardsGui extends Gui {

    private final FlintixCore plugin;

    public RewardsGui(GuiUtil guiUtil, FlintixCore plugin) {
        super(guiUtil);
        this.plugin = plugin;
    }

    @Override
    public String getMenuName() {
        return "Rewards";
    }

    @Override
    public int getSlots() {
        return 45;
    }

    @Override
    public void handleMenu(InventoryClickEvent e) {

    }

    @Override
    public void setItems() {

    }
}
