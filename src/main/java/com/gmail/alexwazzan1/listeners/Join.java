package com.gmail.alexwazzan1.listeners;

import com.gmail.alexwazzan1.ActivePlayer;
import com.gmail.alexwazzan1.Main;
import org.bukkit.Bukkit;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.entity.Player;

public class Join implements Listener {

    private Main plugin;

    public Join(Main plugin) {
        this.plugin = plugin;
        Bukkit.getPluginManager().registerEvents(this, plugin);
    }

    @EventHandler
    public void onJoin(PlayerJoinEvent event) {
        Player p = event.getPlayer();

        Main.lock.lock();
        try {
            // Check if the player has already joined:
            if (Main.players.containsKey(p.getName())) {
                Main.players.remove(p.getName());
            }

            ActivePlayer ap = new ActivePlayer(p.getName());
            Main.players.put(p.getName(), ap);
        } finally {
            Main.lock.unlock();
        }

        if (!p.hasPlayedBefore()) {
            Bukkit.broadcastMessage(plugin.getConfig().getString("firstJoinMessage").replace("<player>", p.getName()));
        } else {
            Bukkit.broadcastMessage(plugin.getConfig().getString("joinMessage").replace("<player>", p.getName()));
        }

        return;
    }

}
