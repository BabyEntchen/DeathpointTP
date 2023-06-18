package com.entidevelopments.deathpointtp;

import net.md_5.bungee.api.chat.ClickEvent;
import net.md_5.bungee.api.chat.TextComponent;
import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.*;


public final class DeathpointTp extends JavaPlugin implements Listener, CommandExecutor {
    public Map<String, List<Integer>> deathPoints = new HashMap<>();
    public String prefix = "§l[Server]§r ";

    @Override
    public void onEnable() {
        Bukkit.getPluginManager().registerEvents(this, this);
        getCommand("back").setExecutor(this);
    }

    @EventHandler
    public void onPlayerDeath(PlayerDeathEvent event) {
        Location deathLocation = event.getEntity().getLocation();
        String deathMessage = event.getDeathMessage();
        event.setDeathMessage(null);


        for (Player player : Bukkit.getServer().getOnlinePlayers()) {
            System.out.println(player.getName());
            if (!player.getUniqueId().equals(event.getEntity().getUniqueId())) {
                if (deathMessage == null) {
                    deathMessage = event.getPlayer().getName() + " ist gestorben!";
                }
                player.sendMessage(prefix + deathMessage);
            } else {
                // Send a clickable message to the player that died
                TextComponent message = new TextComponent(prefix + "§cDu bist gestorben! Klicke §e§l§nhier§r§c um dich zu deinem Todespunkt zu teleportieren!");
                message.setClickEvent(new ClickEvent(ClickEvent.Action.RUN_COMMAND, "/back"));
                player.sendMessage(message);
                deathPoints.remove(player.getName());
                deathPoints.put(player.getName(), new ArrayList<Integer>() {{
                    add(deathLocation.getBlockX());
                    add(deathLocation.getBlockY());
                    add(deathLocation.getBlockZ());
                }});
            }
        }
    }


    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (sender instanceof Player) {
            if (label.equalsIgnoreCase("back")) {
                List<Integer> deathPoint = deathPoints.get(sender.getName());
                if (deathPoint != null) {
                    Objects.requireNonNull(((Player) sender).getPlayer()).teleport(new Location(((Player) sender).getPlayer().getWorld(), deathPoint.get(0), deathPoint.get(1), deathPoint.get(2)));
                    sender.sendMessage(prefix + "Du hast dich zu deinem Todespunkt Teleportiert!");
                    deathPoints.remove(sender.getName());
                } else {
                    sender.sendMessage(prefix + "Du hast keinen aktuellen Todespunkt!");
                }
                return true;

            }
        }
        return false;
    }
}
