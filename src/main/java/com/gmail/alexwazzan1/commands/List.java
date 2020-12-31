package com.gmail.alexwazzan1.commands;

import com.gmail.alexwazzan1.ActivePlayer;
import com.gmail.alexwazzan1.Main;
import com.gmail.alexwazzan1.Team;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Map;

public class List implements CommandExecutor {

    private Main plugin;

    public List(Main plugin) {
        this.plugin = plugin;
        plugin.getCommand("list").setExecutor(this);
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
            if (args.length == 0) {
                ArrayList<ActivePlayer> soloPlayers = new ArrayList<>();
                for (Map.Entry entry : Main.players.entrySet()) {
                    ActivePlayer player = (ActivePlayer) entry.getValue();
                    if (player.hasStarted() && !player.isOnTeam()) {
                        soloPlayers.add(player);
                    }
                }

                if (soloPlayers.size() != 0) {
                    String message = "  ";
                    for (int i = 0; i < soloPlayers.size() - 1; i++) {
                        message += soloPlayers.get(i).getName() + ", ";
                    }
                    message += soloPlayers.get(soloPlayers.size() - 1).getName();
                    p.sendMessage(ChatColor.GREEN + "Individual Players:");
                    p.sendMessage(message);
                }

                for (Map.Entry entry : Main.teams.entrySet()) {
                    Team t = (Team) entry.getValue();
                    ArrayList<ActivePlayer> members = t.getMembers();
                    String message = "  ";
                    for (int i = 0; i < members.size() - 1; i++) {
                        message += members.get(i).getName() + ", ";
                    }
                    message += members.get(members.size() - 1).getName();
                    p.sendMessage(ChatColor.GREEN + t.getName() + ":");
                    p.sendMessage(message);
                }
            } else {
                String teamName = args[0];
                if (Main.teams.containsKey(teamName)) {
                    Team t = Main.teams.get(teamName);
                    ArrayList<ActivePlayer> members = t.getMembers();
                    String message = "  ";
                    for (int i = 0; i < members.size() - 1; i++) {
                        message += members.get(i).getName() + ", ";
                    }
                    message += members.get(members.size() - 1).getName();
                    p.sendMessage(ChatColor.GREEN + t.getName() + ":");
                    p.sendMessage(message);
                } else {
                    p.sendMessage(ChatColor.RED + "This team does not exist.");
                }
            }
        } finally {
            Main.lock.unlock();
        }

        return true;
    }

}
