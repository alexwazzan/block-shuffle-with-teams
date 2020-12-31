package com.gmail.alexwazzan1;

import com.gmail.alexwazzan1.listeners.Move;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.Random;

public class ActivePlayer {

    private String name;
    private boolean started;
    private long startTime;
    // -1: Has not started.
    // 0: Has started.
    // 1: Has finished.
    private int done;
    private Material block;
    private boolean onTeam;
    private String teamName;
    private int timeIndex;

    public ActivePlayer(String player) {
        name = player;
        started = false;
        startTime = 0;
        done = -1;
        block = null;
        onTeam = false;
        teamName = null;
        timeIndex = 0;
    }

    public String getName() {
        return name;
    }

    public boolean hasStarted() {
        return started;
    }

    public long getTime() {
        return startTime;
    }

    public int getState() {
        return done;
    }

    public Material getMaterial() {
        return block;
    }

    public boolean isOnTeam() {
        return onTeam;
    }

    public String getTeamName() {
        return teamName;
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public void start() {
        started = true;
    }

    public void end() {
        started = false;
    }

    public void setTime(long time) {
        startTime = time;
    }

    public void setState(int state) {
        done = state;
    }

    public void setMaterial(Material material) {
        block = material;
    }

    public void setOnTeam() {
        onTeam = true;
    }

    public void setOffTeam() {
        onTeam = false;
    }

    public void setTeamName(String team) {
        teamName = team;
    }

    public void setTimeIndex(int index) {
        timeIndex = index;
    }

    public void incrementTimeIndex() {
        timeIndex++;
    }

    public void resetTimeIndex() {
        timeIndex = 0;
    }

    public static void checkStatus(ActivePlayer ap) {
        if (ap.hasStarted()) {
            Player p = Bukkit.getPlayer(ap.getName());

            Random rand = new Random(System.currentTimeMillis());
            int randomIndex = rand.nextInt(Move.blocks.size());
            Material randomMat = Move.blocks.get(randomIndex);

            // Check if the player has just started:
            if (ap.getState() == -1) {
                ap.setMaterial(randomMat);
                ap.setTime(System.currentTimeMillis());
                ap.setState(0);
                p.sendMessage("Find " + Main.capitalize(randomMat.name().replace("_", " ")) + ". You have " + String.format("%.2f", Move.ALLOWED_TIME / 60.0) + " minutes.");
            }

            // Check if the player has lost:
            if (System.currentTimeMillis() - ap.getTime() >= Move.ALLOWED_TIME * 1000.0) {
                p.sendMessage(ChatColor.RED + "Time's up. You lost.");
                ap.setState(1);
            }

            // Check whether the game has ended:
            if (ap.getState() == 1) {
                ap.setMaterial(randomMat);
                // Reset the starting time:
                ap.setTime(System.currentTimeMillis());
                ap.resetTimeIndex();
                p.sendMessage("Find " + Main.capitalize(randomMat.name().replace("_", " ")) + ". You have " + String.format("%.2f", Move.ALLOWED_TIME / 60.0) + " minutes.");
                ap.setState(0);
            }

            // Print time if necessary:
            double timeLeft = Move.ALLOWED_TIME - (System.currentTimeMillis() - ap.getTime()) / 1000.0;
            if (ap.getTimeIndex() < Move.timesToPrint.length && timeLeft < Move.timesToPrint[ap.getTimeIndex()]) {
                p.sendMessage(ChatColor.RED + String.format("%.0f", Move.timesToPrint[ap.getTimeIndex()]) + " seconds remaining.");
                ap.incrementTimeIndex();
            }
        }

        return;
    }

}
