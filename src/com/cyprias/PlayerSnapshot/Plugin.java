package com.cyprias.PlayerSnapshot;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.sql.SQLException;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.Listener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitTask;
import org.mcstats.Metrics;

import com.cyprias.PlayerSnapshot.Logger;
import com.cyprias.PlayerSnapshot.command.CommandManager;
import com.cyprias.PlayerSnapshot.commands.CreateCommand;
import com.cyprias.PlayerSnapshot.commands.DeleteCommand;
import com.cyprias.PlayerSnapshot.commands.ReloadCommand;
import com.cyprias.PlayerSnapshot.commands.RenameCommand;
import com.cyprias.PlayerSnapshot.commands.RestoreCommand;
import com.cyprias.PlayerSnapshot.commands.RestoreToCommand;
import com.cyprias.PlayerSnapshot.commands.SearchCommand;
import com.cyprias.PlayerSnapshot.commands.VersionCommand;
import com.cyprias.PlayerSnapshot.configuration.Config;
import com.cyprias.PlayerSnapshot.configuration.YML;
import com.cyprias.PlayerSnapshot.listeners.PlayerListener;
import com.cyprias.PlayerSnapshot.utils.ChatUtils;
import com.cyprias.PlayerSnapshot.utils.DateUtil;

public class Plugin extends JavaPlugin {
	// static PluginDescriptionFile description;
	private static Plugin instance = null;
	public static String chatPrefix = "&4[&bPS&4]&r ";

	// public void onLoad() {}

	public void onEnable() {
		instance = this;

		// Check if config.yml exists on disk, copy it over if not. This keeps
		// our comments intact.
		if (!(new File(getDataFolder(), "config.yml").exists())) {
			Logger.info("Copying config.yml to disk.");
			try {
				YML.toFile(getResource("config.yml"), getDataFolder(), "config.yml");
			} catch (FileNotFoundException e) {
				e.printStackTrace();
				return;
			} catch (IOException e) {
				e.printStackTrace();
				return;
			}
		}

		// Register our commands.
		CommandManager cm = new CommandManager();
		cm.registerCommand("version", new VersionCommand());
		cm.registerCommand("reload", new ReloadCommand());
		cm.registerCommand("create", new CreateCommand());
		cm.registerCommand("search", new SearchCommand());
		cm.registerCommand("restore", new RestoreCommand());
		cm.registerCommand("restoreto", new RestoreToCommand());
		cm.registerCommand("delete", new DeleteCommand());
		cm.registerCommand("rename", new RenameCommand());
		
		getCommand("ps").setExecutor(cm);
		
		//Register our event listeners. 
		registerListeners(new PlayerListener());

		if (Config.getBoolean("periodic-snapshots.enabled")){
			long interval = DateUtil.translateTimeStringToSeconds(Config.getString("periodic-snapshots.interval")) * 20L;
			pst = getInstance().getServer().getScheduler().runTaskTimer(this, periodicSnapshot, interval, interval);

		}

		// Cull old snapshots.
		ct = getInstance().getServer().getScheduler().runTaskTimer(this, cullSnapshots, 60*20L, 60*20L);
		
		
		// Start plugin metrics, see how popular our plugin is.
		if (Config.getBoolean("properties.use-metrics")){
			try {
				new Metrics(this).start();
			} catch (IOException e) {}
		}
		
		
		Logger.info("enabled.");
	}

	BukkitTask pst, ct;
	
	Runnable periodicSnapshot = new Runnable() {
		public void run() {
			
			Player[] online = Bukkit.getOnlinePlayers();
			Logger.debug("Snapshoting "+online.length + " players...");
			
			SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMdd.hhmmss"); 
			String date = ft.format(new Date());
			
			String snapName = "periodic-"+date+"-"+Config.getString("periodic-snapshots.lifetime"); 
			
			Logger.info("Snapshoting "+online.length + " players...");
			for (Player p : online) {

				if (p.hasPermission("ps.snapshot.periodic")){
					try {
						Plugin.BackupPlayer(p, snapName);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					} catch (Exception e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				}
			}
			
			
		}
	};
	
	Runnable cullSnapshots = new Runnable() {
		public void run() {
			
			String snapDir = Plugin.getInstance().getDataFolder() + File.separator + "dats";
			
			File snapFolder = new File(snapDir);
			
			//Make sure the dats folder exists.
			snapFolder.mkdirs();
			
			
			File[] listOfFiles = snapFolder.listFiles();
			File[] snaps;
		
			String regex = "(.*)-(.*)-(.*).dat$";
			long age, modified;
			
			for (File f : listOfFiles) {
			//	f = listOfFiles[i];
				if (!f.isFile()) {//Folder
					snaps = f.listFiles();
					
					Logger.debug("Checking " + f.getName() + "'s " +snaps.length + " snapshots.");

					//(.*)-(.*)-(.*).dat$
					for (File snap : snaps) {
						
						if (snap.getName().matches(regex)) {
							
							String lifetime = snap.getName().replaceFirst(regex, "$3");
							
							// Cull seconds.
							int cSeconds = DateUtil.translateTimeStringToSeconds(lifetime);
							
							modified = snap.lastModified() / 1000;
							age =  Plugin.getUnixTime() - modified;
							

							if (age > cSeconds){
								Logger.info("Deleting " +  f.getName() + "'s " + snap.getName());
								snap.delete();
							}
							

							
						}
						
						
					}
					
					
					
					
				}
				
				
			}
			
			
			
		}
	};
	
	/*
	private void BackupPlayers() throws IOException {
		Logger.info("BackupPlayers");
		
		SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss"); 
		
		String date = ft.format(new Date());
		
		String dateDir = instance.getDataFolder() + File.separator + "dats" + File.separator + date;
		
		
		
		Logger.info("date: "+ date);
		
		//Create the folder for our player dats.
		boolean success = new File(dateDir).mkdirs();

		for (Player p : Bukkit.getOnlinePlayers()) {
			File f = getPlayerDat(p.getName());
			
			Logger.info("Found " + f.getName());

			copyFile(f, new File(dateDir + File.separator + f.getName()));

		}
	}
*/
	
	public static void RestorePlayer(File snap, String pName) throws IOException{
		
		File dest = getPlayerDat(pName);
		
		Logger.info("Restoring " + pName + "'s snapshop " + snap.getPath());
		
		copyFile(snap, dest);

	}
	
	
	public static File getLatestSnapshot(String playerName){
		File folder = Plugin.getPlayerDats(playerName);
		File[] listOfFiles = folder.listFiles();
		if (listOfFiles.length > 0){
			// Sort the files by date modified.
			Arrays.sort(listOfFiles, new Comparator<File>(){
			    public int compare(File f1, File f2)
			    {
			        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
			    } });
	
			return listOfFiles[listOfFiles.length-1];
		}
		return null;
	}

	public static File BackupPlayer(Player p, String snapName) throws Exception {
	
		//Plugin.getInstance().getServer().savePlayers();
		
		p.saveData();

		File f = getPlayerDat(p.getName());
		File prevSnap = getLatestSnapshot(p.getName());
		
		Logger.debug(f.getName() + " = " + f.length());
		Logger.debug(prevSnap.getName() + " = " + prevSnap.length() + " : " + (f.length() == prevSnap.length()));
		
		
		if (f != null){
			// Get the current date.
			//SimpleDateFormat ft = new SimpleDateFormat ("yyyy-MM-dd hh:mm:ss"); 
			//String date = ft.format(new Date());
			String playerDir = instance.getDataFolder() + File.separator + "dats" + File.separator + p.getName();
			new File(playerDir).mkdirs();
			
			String snapFile = playerDir + File.separator + snapName + ".dat";
			
			// Create the folder for the dat file.
			File dest = new File(snapFile);// + File.separator + f.getName()

			// Copy file over.
			copyFile(f, dest);
			
			// Return file.
			return dest;
			
		}
		
		return null;

	}

	private static File getPlayerDat(String name){
		//String name = p.getName();
		return new File(instance.getServer().getWorlds().get(0).getWorldFolder().getPath() + File.separator + "players" + File.separator + name+".dat");
	}
	
	public static File getPlayerDats(String name){
		String playerDir = Plugin.getInstance().getDataFolder() + File.separator + "dats" + File.separator + name;
		return new File(playerDir);
	}
	
	
	Listener[] listenerList;

	private void registerListeners(Listener... listeners) {
		PluginManager manager = getServer().getPluginManager();

		listenerList = listeners;

		for (Listener listener : listeners) {
			manager.registerEvents(listener, this);
		}
	}

	public void onDisable() {
		instance.getServer().getScheduler().cancelTasks(instance);

		instance = null;
		Logger.info("disabled.");
	}

	public static void reload() throws SQLException {
		instance.reloadConfig();
	}

	public static final Plugin getInstance() {
		return instance;
	}

	public static long getUnixTime() {
		return (System.currentTimeMillis() / 1000L);
	}

	public static String getFinalArg(final String[] args, final int start) {
		final StringBuilder bldr = new StringBuilder();
		for (int i = start; i < args.length; i++) {
			if (i != start) {
				bldr.append(" ");
			}
			bldr.append(args[i]);
		}
		return bldr.toString();
	}

	public static boolean isInt(final String sInt) {
		try {
			Integer.parseInt(sInt);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static boolean isDouble(final String sDouble) {
		try {
			Double.parseDouble(sDouble);
		} catch (NumberFormatException e) {
			return false;
		}
		return true;
	}

	public static String Round(double val, int pl) {
		String format = "#.";
		for (int i = 1; i <= pl; i++)
			format += "#";

		DecimalFormat df = new DecimalFormat(format);
		return df.format(val);
	}

	public static String Round(double val) {
		return Round(val, 0);
	}

	public static double dRound(double Rval, int Rpl) {
		double p = (double) Math.pow(10, Rpl);
		Rval = Rval * p;
		double tmp = Math.round(Rval);
		return (double) tmp / p;
	}

	public static void copyFile(File sourceFile, File destFile) throws IOException {
	    if(!destFile.exists()) {
	        destFile.createNewFile();
	    }

	    FileChannel source = null;
	    FileChannel destination = null;

	    try {
	        source = new FileInputStream(sourceFile).getChannel();
	        destination = new FileOutputStream(destFile).getChannel();
	        destination.transferFrom(source, 0, source.size());
	    }
	    finally {
	        if(source != null) {
	            source.close();
	        }
	        if(destination != null) {
	            destination.close();
	        }
	    }
	}
	public static boolean checkPermission(CommandSender sender, String permission) {
		if (!sender.hasPermission(permission)) {
			ChatUtils.error(sender, String.format(Config.getString("messages.NoPermission"), permission));
			return false;
		}
		return true;
	}
	
	static String g = ChatColor.GRAY.toString();
	static String w = ChatColor.WHITE.toString();
	public static String secondsToString(long totalSeconds) {

		long days = totalSeconds / 86400;
		long remainder = totalSeconds % 86400;

		long hours = remainder / 3600;
		remainder = totalSeconds % 3600;
		long minutes = remainder / 60;
		long seconds = remainder % 60;

		
		String s = "";
		if (days > 0)
			s += w+days + g+"d";
		if (hours > 0)
			s += w+hours + g+"h";
		if (minutes > 0)
			s += w+minutes + g+"m";
		if (seconds > 0)
			s += w+seconds + g+"s";
		
		return s;
	}
	
	
	public static int scheduleSyncDelayedTask(Runnable arg1, long delay){
		return Plugin.getInstance().getServer().getScheduler().scheduleSyncDelayedTask(instance, arg1, delay);
	}
	
}
