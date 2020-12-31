package com.gmail.alexwazzan1.listeners;

import com.gmail.alexwazzan1.ActivePlayer;
import com.gmail.alexwazzan1.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerQuitEvent;

public class Quit implements Listener {

    private Main plugin;

    public Quit(Main plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onQuit(PlayerQuitEvent event) {
        Player p = (Player) event.getPlayer();

        Main.lock.lock();
        try {
            ActivePlayer ap = Main.players.get(p.getName());
            // If the player is on a team:
            if (ap.isOnTeam()) {
                Main.teams.get(ap.getTeamName()).removeMember(ap);
                // Check if the team is now empty:
                if (Main.teams.get(ap.getTeamName()).getSize() == 0) {
                    Main.teams.remove(ap.getTeamName());
                }
            }
            Main.players.remove(ap.getName());
        } finally {
            Main.lock.unlock();
        }

        return;
    }

}
