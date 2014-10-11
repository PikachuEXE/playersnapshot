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
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerChangedWorldEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import com.cyprias.PlayerSnapshot.Logger;
import com.cyprias.PlayerSnapshot.Plugin;
import com.cyprias.PlayerSnapshot.commands.RestoreCommand;
import com.cyprias.PlayerSnapshot.configuration.Config;

public class PlayerListener implements Listener {

	@EventHandler
	public void onPlayerTeleportEvent(PlayerTeleportEvent e) throws Exception {
		Player p = e.getPlayer();
		if (e.getFrom().getWorld().getName() != e.getTo().getWorld().getName())
		{
			// Player is leaving their previous world.
			Logger.debug(p.getName() + " is leaving world: " + p.getWorld().getName());
			Logger.debug("  Setting event-snapshots.world-leave: " + Config.getBoolean("event-snapshots.world-leave"));
			Logger.debug("  Has permission ps.snapshot.world-leave: " + p.hasPermission("ps.snapshot.world-leave"));
			if (Config.getBoolean("event-snapshots.world-leave")) {
				if (p.hasPermission("ps.snapshot.world-leave")) {
					SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd.hhmmss");
					String date = ft.format(new Date());
		
					String snapName = "leave-" + date + "-" + Config.getString("properties.default-lifetime");
		
					if (Plugin.BackupPlayer(p, snapName) != null)
						Logger.info("Created snapshot (" + snapName + ") for " + p.getName() + ".");
				}
			}
		
		}
	//	Logger.debug("PlayerTeleportEvent " + p.getName() + " from: " + e.getFrom().getWorld().getName() + ", to: " + e.getTo().getWorld().getName());
	}
	
	/*
	@EventHandler
	public void onPlayerMoveEvent(PlayerMoveEvent e) throws Exception {
		Player p = e.getPlayer();
		Logger.debug("PlayerMoveEvent " + p.getName() + " from: " + e.getFrom().getWorld().getName() + ", to: " + e.getTo().getWorld().getName());
	}
	*/
	
	@EventHandler
	public void onPlayerChangedWorldEvent(PlayerChangedWorldEvent e) throws Exception {
		Player p = e.getPlayer();

		if (Config.getBoolean("event-snapshots.world-enter")) {
			if (p.hasPermission("ps.snapshot.world-enter")) {
				SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd.hhmmss");
				String date = ft.format(new Date());
	
				String snapName = "enter-" + date + "-" + Config.getString("properties.default-lifetime");
	
				if (Plugin.BackupPlayer(p, snapName) != null)
					Logger.info("Created snapshot (" + snapName + ") for " + p.getName() + ".");
			}
		}
	}
	
	@EventHandler
	public void onPlayerDeathEvent(PlayerDeathEvent e) throws Exception {

		if (Config.getBoolean("event-snapshots.player-death")) {

			Player p = e.getEntity();
			if (p.hasPermission("ps.snapshot.player-death")) {
				SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd.hhmmss");
				String date = ft.format(new Date());

				String snapName = "death-" + date + "-" + Config.getString("properties.default-lifetime");

				if (Plugin.BackupPlayer(p, snapName) != null)
					Logger.info("Created snapshot (" + snapName + ") for " + p.getName() + ".");

			}

		}
	}

	@EventHandler
	public void onPlayerQuitEvent(PlayerQuitEvent e) {

		final Player p = e.getPlayer();

		if (RestoreCommand.offlineQueue.containsKey(p.getName())) {
			final File f = RestoreCommand.offlineQueue.get(p.getName());

			// Wait a tick before restoring it.
			Plugin.scheduleSyncDelayedTask(new Runnable() {
				public void run() {
					try {
						Plugin.RestorePlayer(f, p.getName());
					} catch (IOException er) {
						er.printStackTrace();
					} catch (Exception e) {
						e.printStackTrace();
					}
				}
			}, 1L);

			RestoreCommand.offlineQueue.remove(p.getName());

		}

	}

	/*
					if (Plugin.BackupPlayer(p, snapName) != null)
						Logger.info("Created snapshot (" + snapName + ") for " + p.getName() + ".");
						
	 */
	
	@EventHandler
	public void onPlayerLoginEvent(PlayerLoginEvent e) {
		if (Config.getBoolean("event-snapshots.player-login")) {

			final Player p = e.getPlayer();
			// Cache their name and UUID for later.
			Plugin.NameUUIDs.put(p.getName(), p.getUniqueId());
			
			if (p.hasPermission("ps.snapshot.player-login")) {
				SimpleDateFormat ft = new SimpleDateFormat("yyyyMMdd.hhmmss");
				String date = ft.format(new Date());

				final String snapName = "login-" + date + "-" + Config.getString("properties.default-lifetime");

				Plugin.scheduleSyncDelayedTask(new Runnable() {
					public void run() {
						try {
							if (Plugin.BackupPlayer(p, snapName) != null)
								Logger.info("Created snapshot (" + snapName + ") for " + p.getName() + ".");
							
						} catch (IOException er) {
							er.printStackTrace();
						} catch (Exception e) {
							e.printStackTrace();
						}
					}
				}, 1L);
				
			}

		}
	}
}
