package net.readycheck.sleepcheck;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitRunnable;

import java.util.ArrayList;
import java.util.List;

public class SleepVote {

    private final JavaPlugin plugin;
    Server server;
    Player instigator; //Person who slept and began sleep vote
    List<Player> players; //List of all players excl instigator
    List<Player> sleepers; //List of all sleeping/sleep voting players incl instigator
    List<Player> wakers; //List of people who haven't voted to sleep or aren't sleeping
    //Wakers list and sleepers list together should create players list.
    boolean votePassed;

    public SleepVote(Player instigator,JavaPlugin plugin) {
        this.plugin = plugin;
        this.server = plugin.getServer();
        this.instigator = instigator;

        players = new ArrayList();
        sleepers = new ArrayList();
        sleepers.add(instigator);

        wakers = new ArrayList<>();
        votePassed = false;

        for(Player p : Bukkit.getOnlinePlayers()) {
            if(p != instigator) {
                players.add(p);
                wakers.add(p);
                p.sendMessage(ChatColor.GOLD + instigator.getDisplayName() + " is sleeping. Skip the night?");
                p.sendMessage(ChatColor.GRAY + "(" + ChatColor.GREEN + "/sleep " + ChatColor.RED + "/nosleep" + ChatColor.GRAY +")");
            }
        }
    }

    public boolean InstigatorWake(Player p) {
        return p == instigator;
    }

    public void addSleeper(Player p) {
        sleepers.add(p);
    }

    public void removeSleeper(Player p) {
        sleepers.remove(p);
        wakers.add(p);
    }

    public float getPercentSleeping() {
        float noSleepers = sleepers.size();
        float noPlayers = players.size() + 1f; //+1 is the instigator being added for calculations
        float sleepPerc = (noSleepers/noPlayers)*100f;
        return sleepPerc;
    }

    public void checkToSkip(Player instigator){
        float interval = 50;

        if(getPercentSleeping() >= 50f) {
            this.votePassed = true;

            new BukkitRunnable() {
                float temptime;
                @Override
                public void run() {
                    temptime = instigator.getWorld().getTime();
                    if(temptime < 1000) {
                        this.cancel();
                    }
                    instigator.getWorld().setTime((long) (temptime + interval));
                }
            }.runTaskTimer(plugin,0,1);
            Util.broadcastMsg(ChatColor.YELLOW + "\nGood Morning!");
        }

    }

    public void printVote(CommandSender sender) {
        float noSleepers = sleepers.size();
        float noPlayers = players.size() + 1f; //+1 is the instigator being added for calculations
        float sleepPerc = getPercentSleeping();

        StringBuilder sb = new StringBuilder();
        sb.append("Current Sleep Vote\n")
                .append("----------------\n");
        if(sleepPerc < 50f) {
            sb.append((int) noSleepers + "/" + (int) noPlayers + " Players wish to sleep (" +  ChatColor.RED + sleepPerc + "%"+ ChatColor.WHITE + ")\n");
        } else {
            sb.append((int) noSleepers + "/" + (int) noPlayers + " Players wish to sleep (" +  ChatColor.GREEN + sleepPerc + "%"+ ChatColor.WHITE + ")\n");
        }

                sb.append(ChatColor.DARK_PURPLE)
                .append("Instigator: " + this.instigator.getDisplayName());

        for(Player p : players) {
            if (sleepers.contains(p)) {
                sb.append(ChatColor.DARK_GREEN);
                sb.append("\n" + p.getDisplayName());
            } else {
                sb.append(ChatColor.DARK_RED);
                sb.append("\n" + p.getDisplayName());
            }
        }
        sb.append("\n");
        sender.sendMessage(sb.toString());
    }







}
