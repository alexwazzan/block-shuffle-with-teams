package com.gmail.alexwazzan1.commands;

import com.gmail.alexwazzan1.ActivePlayer;
import com.gmail.alexwazzan1.Main;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class End implements CommandExecutor {

    private Main plugin;

    public End(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("end").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may execute this command.");
            return true;
        }

        Player p = (Player) sender;

        // Check for an invalid command:
        if (args.length != 0) {
            p.sendMessage(ChatColor.RED + "Invalid command. Try again.");
            return true;
        }

        Main.lock.lock();
        try {
            ActivePlayer ap = Main.players.get(p.getName());

            // Check if the player has not started:
            if (!ap.hasStarted()) {
                p.sendMessage(ChatColor.RED + "Block Shuffle has not started! Use \"/start [TEAM]\" to begin.");
                return true;
            }

            // If the player has already started:
            p.sendMessage(ChatColor.BLUE + "Thanks for playing Block Shuffle!");
            if (ap.isOnTeam()) {
                Main.teams.get(ap.getTeamName()).removeMember(ap);
                // Check if the team is now empty:
                if (Main.teams.get(ap.getTeamName()).getSize() == 0) {
                    Main.teams.remove(ap.getTeamName());
                }
            }
            Main.players.put(p.getName(), new ActivePlayer(p.getName()));
        } finally {
            Main.lock.unlock();
        }

        return true;
    }
}
