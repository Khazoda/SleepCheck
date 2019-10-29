package net.readycheck.sleepcheck;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;

public class Util extends JavaPlugin {

    public static void broadcastMsg(Player instigator,String input) {
        for(Player p: Bukkit.getOnlinePlayers()) {
            if(p!=instigator) {
                p.sendMessage(input);
            }
        }
    }

    public static void broadcastMsg(String input) {
        for(Player p: Bukkit.getOnlinePlayers()) {
                p.sendMessage(input);
        }
    }
}
