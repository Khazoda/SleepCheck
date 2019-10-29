package net.readycheck.sleepcheck;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerBedEnterEvent;
import org.bukkit.event.player.PlayerBedLeaveEvent;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class SleepCheck extends JavaPlugin implements Listener {
    public boolean someoneIsSleeping = false;
    public SleepVote currentVote;

    @Override
    public void onEnable() {
        getServer().getPluginManager().registerEvents(this, this);
        Bukkit.broadcastMessage(ChatColor.BLUE + "SleepCheck V 1.0 ©JuneFaleiro loaded successfully.");

    }
    @Override
    public void onDisable() {

    }

    @Override
    public boolean onCommand(CommandSender sender,
                             Command command,
                             String label,
                             String[] args) {
        if (command.getName().equalsIgnoreCase(("sleepcheck"))) {
            if(currentVote != null) {
                currentVote.printVote(sender);
            } else {
                sender.sendMessage(ChatColor.GOLD + "No sleep vote active");
            }
            return true;
        }


        if (command.getName().equalsIgnoreCase(("sleep"))) {
            if (currentVote!= null) { //if there is a current vote ongoing
                if (sender instanceof Player) {
                    if((Player) sender == currentVote.instigator) {
                        sender.sendMessage(ChatColor.GOLD + "You are the instigator, your vote is automatically a yes");
                    } else {
                        currentVote.addSleeper((Player) sender);
                        sender.sendMessage(ChatColor.GREEN + "You voted to sleep");
                        Util.broadcastMsg((Player) sender,ChatColor.DARK_GREEN + ((Player) sender).getDisplayName() + " voted to sleep (" + currentVote.getPercentSleeping() + "%)");
                            currentVote.checkToSkip(currentVote.instigator);
                    }
                }
            } else {
                sender.sendMessage(ChatColor.GOLD + "No sleep vote active");

            }
            return true;
        }

        if (command.getName().equalsIgnoreCase(("dontsleep"))) {
            if(currentVote!= null) {
                if (sender instanceof Player) {
                    if((Player) sender == currentVote.instigator) {
                        sender.sendMessage(ChatColor.GOLD + "You are the instigator, your vote is automatically a yes");
                    } else {
                        currentVote.instigator.addPotionEffect(new PotionEffect(PotionEffectType.POISON, 20, 1)); //kicks instigator out of bed
                        currentVote.instigator.addPotionEffect(new PotionEffect(PotionEffectType.REGENERATION, 20, 2)); //Heals half a heart
                        currentVote = null;
                        sender.sendMessage(ChatColor.RED + "You cancelled the current sleep vote");
                        Util.broadcastMsg((Player) sender, ChatColor.RED + ((Player) sender).getDisplayName() + " cancelled the current sleep vote.");
                    }
                }
            } else {
                sender.sendMessage(ChatColor.GOLD + "No sleep vote active");
            }
            return true;
        }

        return false;
    }

    @EventHandler
    public void onSleep(PlayerBedEnterEvent event) throws InterruptedException {
        if(event.getBedEnterResult() == PlayerBedEnterEvent.BedEnterResult.OK) {
            Player p = event.getPlayer();
            if(currentVote == null) {
                currentVote = new SleepVote(p,this);
                currentVote.checkToSkip(p);
            } else {
                currentVote.addSleeper(p);
                currentVote.checkToSkip(p);
            }
            someoneIsSleeping = true;
        }
    }

    @EventHandler
    public void onWake(PlayerBedLeaveEvent event) {
        Player p = event.getPlayer();
        if(currentVote.InstigatorWake(p)) {
            if(currentVote == null ) {
                //VOTE HAS BEEN CANCELLED BY A VOTER, INSTIGATOR IS KICKED OUT OF BED BEFORE NEXT LOGIC
            } else if(currentVote.votePassed == true) {
                //VOTE HAS PASSED AND INSTIGATOR HAS AWOKEN THE NEXT MORNING
                currentVote = null;
            } else {
                currentVote = null;
                for (Player player : Bukkit.getOnlinePlayers()) {
                    player.sendMessage(ChatColor.DARK_RED + p.getDisplayName() + " is no longer sleeping. Sleep Vote cancelled");
                }
            }
        } else {
            currentVote.removeSleeper(p);
        }
        someoneIsSleeping = false;


    }

}
