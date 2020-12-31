package com.gmail.alexwazzan1.listeners;

import com.gmail.alexwazzan1.ActivePlayer;
import com.gmail.alexwazzan1.Main;
import com.gmail.alexwazzan1.Team;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerMoveEvent;

import java.util.ArrayList;

public class Move implements Listener {

    private Main plugin;

    public static ArrayList<Material> blocks;
    public static final long ALLOWED_TIME = 60;
    public static final double timesToPrint[] = {30, 10, 5, 4, 3, 2, 1};

    public Move(Main plugin) {
        this.plugin = plugin;

        // Initialize the blocks array:
        blocks = new ArrayList<>();
        for (Material m : Material.values()) {
            if (m.isSolid()) {
                blocks.add(m);
            }
        }

        Bukkit.getPluginManager().registerEvents((Listener) this, plugin);
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();

        Main.lock.lock();
        try {
            ActivePlayer ap = Main.players.get(p.getName());

            if (ap.hasStarted()) {
                // Check if the random block has not been generated:
                if (ap.getMaterial() == null) {
                    return;
                }
                if (ap.isOnTeam()) {
                    Team t = Main.teams.get(ap.getTeamName());

                    Location l = e.getTo();
                    // Check the block that the player is standing on:
                    Block b = l.clone().subtract(0, 1, 0).getBlock();
                    if (b.getType().name().equals(t.getMaterial().name())) {
                        t.sendMessage(ChatColor.GREEN + "Success!");
                        t.setState(1);
                    }
                } else {
                    Location l = e.getTo();
                    // Check the block that the player is standing on:
                    Block b = l.clone().subtract(0, 1, 0).getBlock();
                    if (b.getType().name().equals(ap.getMaterial().name())) {
                        p.sendMessage(ChatColor.GREEN + "Success!");
                        ap.setState(1);
                    }
                }
            }
        } finally {
            Main.lock.unlock();
        }

        return;
    }

}
