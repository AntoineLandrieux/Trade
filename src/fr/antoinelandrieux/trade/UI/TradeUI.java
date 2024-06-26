package fr.antoinelandrieux.trade.UI;

import fr.antoinelandrieux.trade.Main;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

public class TradeUI implements Listener {

    private Main main;

    private Player first;
    private Player second;

    private Inventory firstinv;
    private Inventory secondinv;

    private Boolean firstStatus = false;
    private Boolean secondStatus = false;

    private boolean finish = false;

    private ItemStack VoidItem(Material material) {
        ItemStack glass = new ItemStack(material);
        ItemMeta glassMeta = glass.getItemMeta();
        if (glassMeta != null)
            glassMeta.setDisplayName("§r");
        glass.setItemMeta(glassMeta);
        return glass;
    }

    public TradeUI(Main main, Player f, Player s) {
        this.main = main;
        this.first = f;
        this.second = s;
        this.InitTrade();
    }

    public void InitTrade() {

        this.firstinv = Bukkit.createInventory(null, 9, "§9TRADE: " + this.second.getName());
        this.secondinv = Bukkit.createInventory(null, 9, "§9TRADE: " + this.first.getName());

        ItemStack woolItem = new ItemStack(Material.GREEN_WOOL);
        ItemMeta woolMeta = woolItem.getItemMeta();
        if (woolMeta != null)
            woolMeta.setDisplayName("§r§aOK");
        woolItem.setItemMeta(woolMeta);

        for (int i=0; i<8; i++) {
            if (i != 4) {
                this.firstinv.setItem(i, this.VoidItem(Material.GRAY_STAINED_GLASS_PANE));
                this.secondinv.setItem(i, this.VoidItem(Material.GRAY_STAINED_GLASS_PANE));
            }
        }

        this.firstinv.setItem(0, this.VoidItem(Material.WHITE_STAINED_GLASS_PANE));
        this.secondinv.setItem(0, this.VoidItem(Material.WHITE_STAINED_GLASS_PANE));
        this.firstinv.setItem(8, woolItem);
        this.secondinv.setItem(8, woolItem);

        this.first.openInventory(this.firstinv);
        this.second.openInventory(this.secondinv);

    }

    public void EndTrade() {

        this.finish = true;

        ItemStack f = this.first.getOpenInventory().getTopInventory().getItem(4);
        ItemStack s = this.second.getOpenInventory().getTopInventory().getItem(4);

        if (s != null)
            this.first.getInventory().addItem(s);
        if (f != null)
            this.second.getInventory().addItem(f);

        try { this.first.closeInventory(); } catch (Exception ignored) {}
        try { this.second.closeInventory(); } catch (Exception ignored) {}

        this.firstinv.clear();
        this.secondinv.clear();
        this.first = null;
        this.second = null;

    }

    private void ChangeItem(Player player) {
        ItemStack Item = null;
        if (player == this.first) {
            Item = firstinv.getItem(4);
            if (Item != null)
                this.secondinv.setItem(0, Item);
            else
                this.secondinv.setItem(0, this.VoidItem(Material.WHITE_STAINED_GLASS_PANE));
        } else {
            Item = secondinv.getItem(4);
            if (Item != null)
                this.firstinv.setItem(0, Item);
            else
                this.firstinv.setItem(0, this.VoidItem(Material.WHITE_STAINED_GLASS_PANE));
        }
    }

    private void changeStatus(Player player, Inventory inventory) {

        if (player == this.first)
            this.firstStatus = !this.firstStatus;
        if (player == this.second)
            this.secondStatus = !this.secondStatus;

        if (this.firstStatus == this.secondStatus && this.firstStatus) {
            EndTrade();
            return;
        }

        ItemStack okItem = new ItemStack(Material.GREEN_WOOL);
        ItemMeta okMeta = okItem.getItemMeta();
        if (okMeta != null)
            okMeta.setDisplayName("§r§aOK");
        okItem.setItemMeta(okMeta);

        ItemStack cancelItem = new ItemStack(Material.RED_WOOL);
        ItemMeta cancelMeta = cancelItem.getItemMeta();
        if (cancelMeta != null)
            cancelMeta.setDisplayName("§r§cCANCEL");
        cancelItem.setItemMeta(cancelMeta);

        if (this.firstStatus && player == this.first) {
            ChangeItem(player);
            inventory.setItem(8, cancelItem);
        } else if (player == this.first) {
            ChangeItem(null);
            inventory.setItem(8, okItem);
        }

        if (this.secondStatus && player == this.second) {
            ChangeItem(player);
            inventory.setItem(8, cancelItem);
        } else if (player == this.second) {
            ChangeItem(null);
            inventory.setItem(8, okItem);
        }
    }

    @EventHandler
    public void inventoryClick(InventoryClickEvent event) {

        if (!(event.getWhoClicked() instanceof Player) && !this.finish)
            return;

        Player player = (Player) event.getWhoClicked();

        if (!(player == this.first || player == this.second))
            return;

        if (event.getView().getTitle().startsWith("§9TRADE:")) {
            if (event.getRawSlot() == 8)
                this.changeStatus(player, event.getInventory());
            else if (event.getRawSlot() == 4 || event.getRawSlot() > 8)
                if (!(this.first == player && this.firstStatus) && !(this.second == player && this.secondStatus))
                    return;
            event.setCancelled(true);
        }

    }

    @EventHandler
    public void onInventoryClose(InventoryCloseEvent event) {

        if (!(event.getPlayer() instanceof Player) && !this.finish)
            return;

        Player player = (Player) event.getPlayer();

        if (player == this.first && !this.finish)
            Bukkit.getScheduler().runTaskLater(main, () -> player.openInventory(this.firstinv), 1);
        else if (player == this.second && !this.finish)
            Bukkit.getScheduler().runTaskLater(main, () -> player.openInventory(this.secondinv), 1);

    }

}
