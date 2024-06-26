package fr.antoinelandrieux.trade.Commands;

import fr.antoinelandrieux.trade.Main;
import fr.antoinelandrieux.trade.UI.TradeUI;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Trade implements CommandExecutor {

    private Main main;
    private static final Map<String, String> playerPlayerMap = new HashMap<>();

    public Trade(Main main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if (!(sender instanceof Player))
            return false;

        FileConfiguration config = main.getConfig();
        Player player = (Player) sender;

        if (label.equalsIgnoreCase("tradeaccept") || label.equalsIgnoreCase("tradedeny")) {

            if (!playerPlayerMap.containsKey(player.getName().toLowerCase())) {
                player.sendMessage(Objects.requireNonNull(config.getString("trade.msg.error4")));
                return true;
            }

            int denyOrAccept = 6;

            if (args[0].equalsIgnoreCase("deny"))
                denyOrAccept = 5;

            String playerName = player.getName().toLowerCase();
            Player target = Bukkit.getPlayer(playerPlayerMap.get(playerName));

            if (target == null) {
                player.sendMessage(Objects.requireNonNull(config.getString("trade.msg.error3")));
                return true;
            }

            target.sendMessage(Objects.requireNonNull(config.getString("trade.msg.error" + (denyOrAccept - 2))).replace("$", playerName));
            player.sendMessage(Objects.requireNonNull(config.getString("trade.msg.error" + denyOrAccept)));

            if (denyOrAccept == 6)
                main.getServer().getPluginManager().registerEvents(new TradeUI(main, player, target), main);

            playerPlayerMap.remove(playerName);

        } else if (label.equalsIgnoreCase("trade")) {

            if (args.length != 1)
                return false;

            Player target = Bukkit.getPlayer(args[0]);
            String targetName = args[0].toLowerCase();

            if (target != null) {

                if (player.equals(target)) {
                    player.sendMessage(Objects.requireNonNull(config.getString("trade.msg.error1")));
                    return true;
                }

                if (playerPlayerMap.containsKey(targetName)) {
                    Player send = Bukkit.getPlayer(playerPlayerMap.get(targetName));
                    if (send != null)
                        send.sendMessage(Objects.requireNonNull(config.getString("trade.msg.error2")));
                    playerPlayerMap.remove(targetName);
                }

                playerPlayerMap.put(targetName, player.getName());

                player.sendMessage(Objects.requireNonNull(config.getString("trade.msg.info1")).replace("$", target.getName()));
                target.sendMessage(Objects.requireNonNull(config.getString("trade.msg.info2")).replace("$", target.getName()));

            } else {
                player.sendMessage(Objects.requireNonNull(config.getString("trade.msg.error3")));
            }
        }
        return true;
    }
}
