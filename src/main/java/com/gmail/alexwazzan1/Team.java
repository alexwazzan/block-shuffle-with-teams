package com.gmail.alexwazzan1;

import com.gmail.alexwazzan1.listeners.Move;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Random;

public class Team {

    private ArrayList<ActivePlayer> members;
    private String name;
    private boolean started;
    private long startTime;
    // -1: Has not started.
    // 0: Has started.
    // 1: Has finished.
    private int done;
    private Material block;
    private int timeIndex;

    public Team(String teamName) {
        members = new ArrayList<>();
        name = teamName;
        started = true;
        startTime = 0;
        done = -1;
        block = null;
        timeIndex = 0;
    }

    public ArrayList<ActivePlayer> getMembers() {
        return members;
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

    public boolean hasPlayer(ActivePlayer ap) {
        return members.contains(ap);
    }

    public Material getMaterial() {
        return block;
    }

    public int getTimeIndex() {
        return timeIndex;
    }

    public int getSize() {
        return members.size();
    }

    public void addMember(ActivePlayer ap) {
        ap.start();
        ap.setTime(startTime);
        ap.setState(done);
        ap.setMaterial(block);
        ap.setOnTeam();
        ap.setTeamName(name);
        ap.setTimeIndex(timeIndex);
        members.add(ap);
    }

    public void removeMember(ActivePlayer ap) {
        // Possibly pointless:
        ap.end();

        members.remove(ap);
    }

    public void start() {
        started = true;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).start();
        }
    }

    public void end() {
        started = false;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).end();
        }
    }

    public void setTime(long time) {
        startTime = time;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setTime(time);
        }
    }

    public void setState(int state) {
        done = state;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setState(state);
        }
    }

    public void setMaterial(Material material) {
        block = material;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setMaterial(material);
        }
    }

    public void setTimeIndex(int index) {
        timeIndex = index;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).setTimeIndex(index);
        }
    }

    public void incrementTimeIndex() {
        timeIndex++;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).incrementTimeIndex();
        }
    }

    public void resetTimeIndex() {
        timeIndex = 0;
        for (int i = 0; i < members.size(); i++) {
            members.get(i).resetTimeIndex();
        }
    }

    public void sendMessage(String message) {
        for (int i = 0; i < members.size(); i++) {
            Player p = Bukkit.getPlayer(members.get(i).getName());
            p.sendMessage(message);
        }
    }

    public static void checkStatus(Team t) {
        Random rand = new Random(System.currentTimeMillis());
        int randomIndex = rand.nextInt(Move.blocks.size());
        Material randomMat = Move.blocks.get(randomIndex);

        if (t.getState() == -1) {
            t.setMaterial(randomMat);
            t.setTime(System.currentTimeMillis());
            t.setState(0);
            t.sendMessage("Find " + Main.capitalize(randomMat.name().replace("_", " ")) + ". You have " + String.format("%.2f", Move.ALLOWED_TIME / 60.0) + " minutes.");
        }

        if (System.currentTimeMillis() - t.getTime() >= Move.ALLOWED_TIME * 1000.0) {
            t.sendMessage(ChatColor.RED + "Time's up. You lost.");
            t.setState(1);
        }

        // Check whether the game has ended:
        if (t.getState() == 1) {
            t.setMaterial(randomMat);
            // Reset the starting time:
            t.setTime(System.currentTimeMillis());
            t.resetTimeIndex();
            t.sendMessage("Find " + Main.capitalize(randomMat.name().replace("_", " ")) + ". You have " + String.format("%.2f", Move.ALLOWED_TIME / 60.0) + " minutes.");
            t.setState(0);
        }

        // Print time if necessary:
        double timeLeft = Move.ALLOWED_TIME - (System.currentTimeMillis() - t.getTime()) / 1000.0;
        if (t.getTimeIndex() < Move.timesToPrint.length && timeLeft < Move.timesToPrint[t.getTimeIndex()]) {
            t.sendMessage(ChatColor.RED + String.format("%.0f", Move.timesToPrint[t.getTimeIndex()]) + " seconds remaining.");
            t.incrementTimeIndex();
        }

        return;
    }

}
