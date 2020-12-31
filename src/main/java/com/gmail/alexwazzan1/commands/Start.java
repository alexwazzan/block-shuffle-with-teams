package com.gmail.alexwazzan1.commands;

import com.gmail.alexwazzan1.ActivePlayer;
import com.gmail.alexwazzan1.Main;
import com.gmail.alexwazzan1.Team;
import com.gmail.alexwazzan1.listeners.Move;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Start implements CommandExecutor {

    private Main plugin;

    public Start(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("start").setExecutor(this);
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (!(sender instanceof Player)) {
            sender.sendMessage("Only players may execute this command.");
            return true;
        }

        Player p = (Player) sender;

        // Check for an invalid command:
        if (args.length >= 2) {
            p.sendMessage(ChatColor.RED + "The name of a team cannot contain any spaces. Try again.");
            return true;
        }

        Main.lock.lock();
        try {
            ActivePlayer ap = Main.players.get(p.getName());

            // Check if the player has already started:
            if (ap.hasStarted()) {
                p.sendMessage(ChatColor.RED + "Block Shuffle has not ended! Use \"/end\" to stop.");
                return true;
            }

            // If the player has specified a team:
            p.sendMessage(ChatColor.GREEN + "Welcome to Block Shuffle!");
            if (args.length == 1) {
                String teamName = args[0];
                if (Main.teams.containsKey(teamName)) {
                    Team t = Main.teams.get(teamName);
                    t.addMember(ap);
                    if (t.getMaterial() != null) {
                        double timeLeft = (Move.ALLOWED_TIME - (System.currentTimeMillis() - t.getTime()) / 1000.0) / 60.0;
                        p.sendMessage("Find " + Main.capitalize(t.getMaterial().name().replace("_", " ")) + ". You have " + String.format("%.2f", timeLeft) + " minutes.");
                    }
                } else {
                    Team t = new Team(teamName);
                    t.addMember(ap);
                    Main.teams.put(teamName, t);
                }
            // If the player did not specify a team:
            } else {
                Main.players.get(p.getName()).start();
            }
        } finally {
            Main.lock.unlock();
        }

        return true;
    }

}
