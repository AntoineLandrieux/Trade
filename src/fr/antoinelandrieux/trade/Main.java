package fr.antoinelandrieux.trade;

import fr.antoinelandrieux.trade.Commands.Trade;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.Objects;

public class Main extends JavaPlugin {

    @Override
    public void onEnable() {
        Objects.requireNonNull(this.getCommand("trade")).setExecutor(new Trade(this));
        Objects.requireNonNull(this.getCommand("tradeaccept")).setExecutor(new Trade(this));
        Objects.requireNonNull(this.getCommand("tradedeny")).setExecutor(new Trade(this));

        System.out.println("Plugin Trade enable");
    }

    @Override
    public void onDisable() {
        System.out.println("Plugin Trade disable");
    }

}
