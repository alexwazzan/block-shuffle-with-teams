package com.gmail.alexwazzan1;

import com.gmail.alexwazzan1.commands.Start;
import com.gmail.alexwazzan1.listeners.Quit;
import org.bukkit.plugin.java.JavaPlugin;
import com.gmail.alexwazzan1.commands.End;
import com.gmail.alexwazzan1.commands.List;
import com.gmail.alexwazzan1.listeners.Join;
import com.gmail.alexwazzan1.listeners.Move;

import java.util.concurrent.locks.ReentrantLock;
import java.util.HashMap;
import java.util.Map;

public class Main extends JavaPlugin {

    public static HashMap<String, ActivePlayer> players;
    public static HashMap<String, Team> teams;
    public static ReentrantLock lock;

    @Override
    public void onEnable() {
        saveDefaultConfig();
        getLogger().info("onEnable is called!");

        players = new HashMap<>();
        teams = new HashMap<>();
        lock = new ReentrantLock();

        new Start(this);
        new End(this);
        new List(this);
        new Join(this);
        new Move(this);
        new Quit(this);

        Thread th = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    lock.lock();
                    try {
                        for (Map.Entry entry : teams.entrySet()) {
                            Team t = (Team) entry.getValue();
                            Team.checkStatus(t);
                        }
                        for (Map.Entry entry : players.entrySet()) {
                            ActivePlayer ap = (ActivePlayer) entry.getValue();
                            if (!ap.isOnTeam()) {
                                ActivePlayer.checkStatus(ap);
                            }
                        }
                    } finally {
                        lock.unlock();
                    }
                }
            }
        });
        th.start();
    }

    public static String capitalize(String input) {
        String words[] = input.split("\\s");
        String capitalized = "";
        for (String word : words) {
            String first = word.substring(0, 1);
            String afterFirst = word.substring(1).toLowerCase();
            capitalized += first.toUpperCase() + afterFirst + " ";
        }
        return capitalized.trim();
    }

}
