package net.readycheck.sleepcheck;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.List;

public class SleepVote {

    Player instigator; //Person who slept and began sleep vote
    List<Player> players; //List of all players excl instigator
    List<Player> sleepers; //List of all sleeping/sleep voting players incl instigator
    List<Player> wakers; //List of people who haven't voted to sleep or aren't sleeping
    //Wakers list and sleepers list together should create players list.
    boolean votePassed;

    public SleepVote(Player instigator) {
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
                p.sendMessage(ChatColor.GRAY + "(" + ChatColor.GREEN + "/y " + ChatColor.RED + "/n" + ChatColor.GRAY +")");
            }
        }
    }

    public boolean InstigatorWake(Player p) {
        if(p == instigator) {
            return true;
        } else {
            return false;
        }
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

    public void checkToSkip(Player instigator) throws InterruptedException {
        float interval = 100;
        float morning = 24000;
        float currentTime = instigator.getWorld().getTime();
        float difference = morning - currentTime;
        float incrementer = difference/interval;
        float temp;

        if(getPercentSleeping() >= 50f) {
            Util.broadcastMsg(ChatColor.YELLOW + "\nSleep vote passed... good morning everyone!");
            this.votePassed = true;
            for(float i = 0; i < incrementer; i++) {
                temp = instigator.getWorld().getTime();
                instigator.getWorld().setTime((long)(temp+interval));
                Thread.sleep((long)20);
            }
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
