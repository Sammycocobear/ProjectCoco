package me.scb.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class EasterEggListener implements Listener {
    private static final Set<UUID> uuids = new HashSet<>();
    static {
        uuids.add(UUID.fromString("7159aaec-c7f2-4fc2-86cc-09e3fa303c40"));
        uuids.add(UUID.fromString("4fe01b72-bacf-4dd5-946f-f3725d4777e4"));
    }
    private static boolean hasEasterEgg = false;

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        final Player player = e.getPlayer();
        if (uuids.contains(player.getUniqueId())){
            if (e.getMessage().equalsIgnoreCase("coco is so cute")){
                hasEasterEgg = !hasEasterEgg;
                if (hasEasterEgg()){
                    player.sendMessage("U enabled rainbow mode");
                }else{
                    player.sendMessage("U disabled rainbow mode");

                }
                e.setCancelled(true);
            }

        }

    }


    public static boolean hasEasterEgg(){
        return hasEasterEgg;
    }

}
