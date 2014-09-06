package me.capit.classes;

import org.bukkit.entity.Player;

public class DatabaseUpdater implements Runnable {
	private ClassSystemMain plugin;
	
	public DatabaseUpdater(ClassSystemMain plugin){
		this.plugin = plugin;
	}
	
	public void run() {
		for (Player p : plugin.getServer().getOnlinePlayers()){
			plugin.putStats(p);
		}
	}

}
