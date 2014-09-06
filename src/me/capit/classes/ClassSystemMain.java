package me.capit.classes;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.logging.Logger;

import me.capit.ds_mc.DSMCMain;
import me.capit.ds_mc.DSMCMain.DSMCState;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;

public class ClassSystemMain extends JavaPlugin {
	public static Logger logger;
	
	public static HashMap<String, HashMap<String,Object>> data = new HashMap<String, HashMap<String,Object>>();
	
	public void putStats(Player p){
		HashMap<String,Object> map = data.get(p.getUniqueId().toString());
		if (map!=null){
			String query = "INSERT INTO entropy_users (uuid,capit_id,current_class,rifleman,dreadnought,tinkerer,marksman,aristocrat) VALUES (";
			query += "'"+map.get("uuid")+"',";
			query += map.get("capit_id")+",";
			query += "'"+map.get("current_class")+"',";
			query += map.get("rifleman")+",";
			query += map.get("dreadnought")+",";
			query += map.get("tinkerer")+",";
			query += map.get("marksman")+",";
			query += map.get("aristocrat")+")";
			try {
				DSMCMain.updateDB(query);
			} catch (Exception e) {
				logger.warning("Failed to open query to database!");
				e.printStackTrace();
			}
		} else {
			logger.info("Failed to load data object.");
			HashMap<String,Object> newmap = getStats(p);
			if (newmap!=null){ putStats(p);} else {logger.warning("Failed to construct new map!");}
		}
	}
	
	public HashMap<String,Object> getStats(Player p){
		HashMap<String,Object> map = new HashMap<String,Object>();
		try {
			ResultSet res = DSMCMain.queryDB("SELECT * FROM entropy_users WHERE uuid='"+p.getUniqueId().toString()+"'");
			if (res!=null && res.next()){
				logger.info("Queried database for "+p.getUniqueId().toString()+" and got response.");
				boolean first = true;
				res.first();
				while (res.next()){
					if (first){
						first=false;
						map.put("uuid", res.getString("uuid"));
						map.put("capit_id", res.getInt("capit_id"));
						map.put("current_class", res.getString("current_class"));
						map.put("rifleman", res.getInt("rifleman"));
						map.put("dreadnought", res.getInt("dreadnought"));
						map.put("tinkerer", res.getInt("tinkerer"));
						map.put("marksman", res.getInt("marksman"));
						map.put("aristocrat", res.getInt("aristocrat"));
						data.put(p.getUniqueId().toString(),map);
						return map;
					}
				}
			} else {
				logger.info("Queried database for "+p.getUniqueId().toString()+" and got no response!");
				logger.info("Tried to get stats for player, but no stats were found! Creating new stats...");
				map.put("uuid", p.getUniqueId().toString());
				map.put("capit_id", 0);
				map.put("current_class", "none");
				map.put("rifleman", 0);
				map.put("dreadnought", 0);
				map.put("tinkerer", 0);
				map.put("marksman", 0);
				map.put("aristocrat", 0);
				data.put(p.getUniqueId().toString(),map);
				return map;
			}
		} catch (SQLException e) {
			logger.warning("Failed to open query to database!");
			e.printStackTrace();
		}
		return null;
	}
	
	@Override
	public void onEnable(){
		logger = getLogger();
		logger.info(DSMCMain.formHeader(ClassSystemMain.class));
		logger.info("Loading Entropy class system...");
		if (getPlugin(DSMCMain.class).getState()==DSMCState.READY){
			logger.info("Successfully hooked DataShuttle decependency.");
			this.saveDefaultConfig();
			
			logger.info("Scheduling tasks...");
			DatabaseUpdater dbupdate = new DatabaseUpdater(this);
			BukkitScheduler scheduler = Bukkit.getServer().getScheduler();
			int delay = this.getConfig().getInt("db.update-delay");
			scheduler.scheduleSyncRepeatingTask(this, dbupdate, 0, delay*60);
			
			logger.info("Registering commands and events...");
			ClassCommands cmds = new ClassCommands(this);
			this.getCommand("class").setExecutor(cmds);
			ClassEvents ce = new ClassEvents(this);
			this.getServer().getPluginManager().registerEvents(ce, this);
			
			if (this.getServer().getOnlinePlayers().length>0){
				logger.info("Preinitializing database... (Players already connected!)");
				for (Player p : this.getServer().getOnlinePlayers()){
					getStats(p);
				}
			}
			
			logger.info("Class system loaded and ready.");
			logger.info(DSMCMain.formFooter(ClassSystemMain.class));
		} else {
			logger.info("Failed to hook into DataShuttle! Shutting down!");
			getPluginLoader().disablePlugin(this);
		}
	}
	
	@Override
	public void onDisable(){
		logger.info(DSMCMain.formHeader(ClassSystemMain.class));
		logger.info("Wrapping up class handler...");
		
		for (Player p : this.getServer().getOnlinePlayers()){
			putStats(p);
		}
		
		logger.info("Class system shut down.");
		logger.info(DSMCMain.formFooter(ClassSystemMain.class));
	}
	
}
