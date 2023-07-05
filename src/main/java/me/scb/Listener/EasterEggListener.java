package me.scb.Listener;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;

import java.util.*;

public class EasterEggListener implements Listener {
    private static final Map<UUID,Boolean> uuids = new HashMap<>();
    static {
        uuids.put(UUID.fromString("7159aaec-c7f2-4fc2-86cc-09e3fa303c40"),false);
        uuids.put(UUID.fromString("4fe01b72-bacf-4dd5-946f-f3725d4777e4"),false);
        uuids.put(UUID.fromString("0b0027d3-49d1-4d36-bf84-4bf7b18be885"),false);
    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e){
        final Player player = e.getPlayer();
        if (uuids.containsKey(player.getUniqueId())){
            if (e.getMessage().equalsIgnoreCase("coco is so cute")){
                uuids.put(player.getUniqueId(),!uuids.get(player.getUniqueId()));
                if (hasEasterEgg(player)){
                    player.sendMessage("U enabled rainbow mode");
                }else{
                    player.sendMessage("U disabled rainbow mode");

                }
                e.setCancelled(true);
            }

        }

    }


    public static boolean hasEasterEgg(Player player){
        return uuids.getOrDefault(player.getUniqueId(),false);
    }

}
