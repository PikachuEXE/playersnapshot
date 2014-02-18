package com.cyprias.PlayerSnapshot.listeners;

import java.io.File;
import java.io.IOException;

import javax.xml.parsers.ParserConfigurationException;

import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;

import com.cyprias.PlayerSnapshot.Plugin;
import com.cyprias.PlayerSnapshot.commands.RestoreCommand;


public class PlayerListener implements Listener {

	
	@EventHandler(priority = EventPriority.HIGHEST)
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
			
			

		}
		
		
	}
}
