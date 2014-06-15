package com.cyprias.PlayerSnapshot.commands;

import java.io.File;
import java.io.FileNotFoundException;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.World;
import org.bukkit.command.CommandSender;

import com.cyprias.PlayerSnapshot.Plugin;
import com.cyprias.PlayerSnapshot.command.Command;
import com.cyprias.PlayerSnapshot.command.CommandAccess;
import com.cyprias.PlayerSnapshot.configuration.Config;
import com.cyprias.PlayerSnapshot.utils.ChatUtils;

public class SearchCommand implements Command {
	public void listCommands(CommandSender sender, List<String> list) {
		if (sender.hasPermission("ps.search"))
			list.add("/%s search - Search for a snapshop.");
	}

	public static HashMap<String, File[]> previousResults = new HashMap<String, File[]>();
	
	public static HashMap<String, String> previousPlayer = new HashMap<String, String>();
	

	
	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) throws FileNotFoundException {
		if (!Plugin.checkPermission(sender, "ps.search"))
			return false;
		
		
		if (args.length == 0){
			ChatUtils.send(sender, "&7/ps search <name> [page#]");
			return true;
		}
		
		String pName = args[0];

		int page = 0;
		if (args.length > 1) {// && args[1].equalsIgnoreCase("compact"))
			if (Plugin.isInt(args[1])) {
				page = Math.abs(Integer.parseInt(args[1]));
			} else {
				ChatUtils.error(sender, "Invalid page number: " +  args[1]);
				return true;
			}
		}
		
		
		
		Plugin inst = Plugin.getInstance();
		Server s = inst.getServer();

		File folder = Plugin.getPlayerDats(pName);
		
		if (folder == null || folder.listFiles() == null){
			
			ChatUtils.error(sender, "Unknown player: " + pName);
			return true;
		}
		
		
		File[] listOfFiles = folder.listFiles();

		ChatUtils.send(sender, "&7Found &f" + listOfFiles.length + " &7snaphots for &f" + pName + "&7.");
		
		// Sort the files by date modified.
		Arrays.sort(listOfFiles, new Comparator<File>(){
		    public int compare(File f1, File f2)
		    {
		        return Long.valueOf(f1.lastModified()).compareTo(f2.lastModified());
		    } });
		
		
		int rows = listOfFiles.length;
		if (rows == 0 ){
			ChatUtils.error(sender, "No results for " + pName);
			return true;
		}
		
		int rowsPerPage = Config.getInt("properties.rows-per-page");
		
		int maxPages = (int) Math.ceil((float) rows / (float) rowsPerPage);	// 10 lines per page.
		
		// If user didn't put a page number, show the last page.
		if (page == 0)
		{
			page = maxPages;
		}

		if (rows > rowsPerPage){
			ChatUtils.send(sender, String.format("&7Page &f%s &7of &f%s", page, maxPages));
		}
		int start = ((page-1) * rowsPerPage);
		int end = start + rowsPerPage;
		if (end > rows)
			end = rows;
		
		
		
		
		File f ;
		long age, modified;
		World w;
		for (int i = start; i < end; i++) {
		//for (int i = 0; i < listOfFiles.length; i++) {
			f = listOfFiles[i];
			
			modified = f.lastModified() / 1000;
			
			age =  Plugin.getUnixTime() - modified;

			w = Plugin.getDatWorld(f);
			//w.getName()

			ChatUtils.send(sender, String.format("&a§l%s&7 &f%s&7 (&f%s&7) &f%s", i, f.getName(), w.getName(), Plugin.secondsToString(age)));
		}
		
		
		previousPlayer.put(sender.getName(), pName);
		previousResults.put(sender.getName(), listOfFiles);
		
	
		return true;
	}

	
	
	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, "ps.search", "/%s search <player> [page#]", cmd);
	}

	public boolean hasValues() {
		return false;
	}
	
}
