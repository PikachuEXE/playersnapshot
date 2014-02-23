package com.cyprias.PlayerSnapshot.listeners;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import com.cyprias.PlayerSnapshot.Logger;
import com.cyprias.PlayerSnapshot.Plugin;
import com.cyprias.PlayerSnapshot.commands.RestoreCommand;
import com.cyprias.PlayerSnapshot.configuration.Config;


public class PlayerListener implements Listener {


	
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent e) throws Exception {
		
		if (Config.getBoolean("event-snapshots.player-death")){
			
			Player p = e.getEntity();
			if (p.hasPermission("ps.snapshot.player-death")){
				SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMdd.hhmmss"); 
				String date = ft.format(new Date());
				
				String snapName = "death-"+date+"-"+Config.getString("properties.default-lifetime"); 

				if (Plugin.BackupPlayer(p, snapName) != null)
					Logger.info("Created snapshot (" + snapName +") for " + p.getName() + ".");

			}
			
			
			
		}
	}
	
	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {
		
		final Player p = e.getPlayer();
		
		if (RestoreCommand.offlineQueue.containsKey(p.getName())){
			final File f = RestoreCommand.offlineQueue.get(p.getName());
			
			// Wait a tick before restoring it.
			Plugin.scheduleSyncDelayedTask(new Runnable() {
				public void run() {
					try {
						Plugin.RestorePlayer(f, p.getName());
					} catch (IOException er) {er.printStackTrace();}
				}
			}, 1L);
			
			RestoreCommand.offlineQueue.remove(p.getName());
			

		}
		
		
	}
}
