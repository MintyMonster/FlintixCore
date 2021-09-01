package uk.co.minty_studios.flintixcore.Gui.rewards;

import org.bukkit.Material;
import org.bukkit.Statistic;
import org.bukkit.event.inventory.InventoryClickEvent;
import uk.co.minty_studios.flintixcore.FlintixCore;
import uk.co.minty_studios.flintixcore.Gui.handler.Gui;
import uk.co.minty_studios.flintixcore.Gui.handler.GuiUtil;
import uk.co.minty_studios.flintixcore.database.DatabaseHandler;
import uk.co.minty_studios.flintixcore.utils.GuiItems;

import java.util.Arrays;
import java.util.stream.Collectors;

public class RewardsGui extends Gui {

    private final FlintixCore plugin;
    private final DatabaseHandler database;
    private final GuiItems guiItems;

    public RewardsGui(GuiUtil guiUtil, FlintixCore plugin, DatabaseHandler database, GuiItems guiItems) {
        super(guiUtil);
        this.plugin = plugin;
        this.database = database;
        this.guiItems = guiItems;
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

        for(int i = 0; i < getSlots(); i++){
            if(i == 4){
                inventory.setItem(i, guiItems.getPlayerSkull(super.guiUtil.getOwner(),
                        plugin.parsePlaceholders(plugin.getConfig().getString("settings.gui.rewards.player-head.display-name")
                                .replace("%displayname%", super.guiUtil.getOwner().getDisplayName())),
                        plugin.getConfig().getStringList("settings.gui.rewards.player-head.lore")
                                .stream().map(line -> plugin.parsePlaceholders(line)
                                        .replace("%joined%", String.valueOf(database.getPlayerMap().get(super.guiUtil.getOwner().getUniqueId()).getFirstLogin()))
                                        .replace("%playtime%", plugin.getTime(super.guiUtil.getOwner().getStatistic(Statistic.PLAY_ONE_MINUTE)))
                                        .replace("%streak%", String.valueOf(database.getPlayerMap().get(super.guiUtil.getOwner().getUniqueId()).getStreak()))
                                        .replace("%money%", String.valueOf(database.getPlayerMap().get(super.guiUtil.getOwner().getUniqueId()).getMoney())))
                                .collect(Collectors.toList()))); //

                continue;
            }

            if(i == 11){
                inventory.setItem(i, guiItems.getCustomSkull(plugin.parsePlaceholders(plugin.getConfig().getString("settings.gui.rewards.discord.display-name")),
                        GuiItems.SkullType.DISCORD.getBase(),
                        plugin.getConfig().getStringList("settings.gui.rewards.discord.lore")
                                .stream().map(line -> plugin.parsePlaceholders(line)).collect(Collectors.toList())));
            }

            if(i == 12){
                inventory.setItem(i, guiItems.getCustomSkull(plugin.parsePlaceholders(plugin.getConfig().getString("settings.gui.rewards.twitter.display-name")),
                        GuiItems.SkullType.TWITTER.getBase(),
                        plugin.getConfig().getStringList("settings.gui.rewards.twitter.lore")
                                .stream().map(line -> plugin.parsePlaceholders(line)).collect(Collectors.toList())));
            }

            if (i == 13) {
                inventory.setItem(i, guiItems.getCustomSkull(plugin.parsePlaceholders(plugin.getConfig().getString("settings.gui.rewards.youtube.display-name")),
                        GuiItems.SkullType.YOUTUBE.getBase(),
                        plugin.getConfig().getStringList("settings.gui.rewards.youtube.lore")
                                .stream().map(line -> plugin.parsePlaceholders(line)).collect(Collectors.toList())));
            }

            if(i == 14){
                inventory.setItem(i, guiItems.getCustomSkull(plugin.parsePlaceholders(plugin.getConfig().getString("settings.gui.rewards.website.display-name")),
                        GuiItems.SkullType.WEBSITE.getBase(),
                        plugin.getConfig().getStringList("settings.gui.rewards.website.lore")
                                .stream().map(line -> plugin.parsePlaceholders(line)).collect(Collectors.toList())));
            }

            if(i == 15) {
                inventory.setItem(i, guiItems.getCustomSkull(plugin.parsePlaceholders(plugin.getConfig().getString("settings.gui.rewards.tiktok.display-name")),
                        GuiItems.SkullType.TIKTOK.getBase(),
                        plugin.getConfig().getStringList("settings.gui.rewards.tiktok.lore")
                                .stream().map(line -> plugin.parsePlaceholders(line)).collect(Collectors.toList())));
            }



            inventory.setItem(i, guiItems.getCustomItem(Material.BLACK_STAINED_GLASS_PANE, "-", ""));
        }

    }
}
