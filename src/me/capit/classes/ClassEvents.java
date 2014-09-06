package me.capit.classes;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public class ClassEvents implements Listener {
	ClassSystemMain plugin;
	public ClassEvents(ClassSystemMain plugin){
		this.plugin = plugin;
	}
	
	
	@EventHandler
	public void playerJoined(PlayerJoinEvent e){
		Player p = e.getPlayer();
		plugin.getStats(p);
	}
	
	@EventHandler
	public void playerLeft(PlayerQuitEvent e){
		Player p = e.getPlayer();
		plugin.putStats(p);
	}
	
}
