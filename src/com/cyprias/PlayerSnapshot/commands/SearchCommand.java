package com.cyprias.PlayerSnapshot.commands;

import java.io.File;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import org.bukkit.Server;
import org.bukkit.command.CommandSender;
import com.cyprias.PlayerSnapshot.Plugin;
import com.cyprias.PlayerSnapshot.command.Command;
import com.cyprias.PlayerSnapshot.command.CommandAccess;
import com.cyprias.PlayerSnapshot.utils.ChatUtils;

public class SearchCommand implements Command {
	public void listCommands(CommandSender sender, List<String> list) {
		if (sender.hasPermission("ps.search"))
			list.add("/%s search - Search for a snapshop.");
	}

	public static HashMap<String, File[]> previousResults = new HashMap<String, File[]>();
	
	public static HashMap<String, String> previousPlayer = new HashMap<String, String>();
	

	
	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if (!Plugin.checkPermission(sender, "ps.search"))
			return false;
		
		
		if (args.length == 0){
			ChatUtils.send(sender, "&7Add a player's name.");
			return true;
		}
		
		String pName = args[0];

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
		
		
		File f ;

		long age, modified;
		for (int i = 0; i < listOfFiles.length; i++) {
			f = listOfFiles[i];
			
			modified = f.lastModified() / 1000;
			
			age =  Plugin.getUnixTime() - modified;

			ChatUtils.send(sender, "&7[&a"+i + "&7] &f"+ f.getName() + "&7: &f" + Plugin.secondsToString(age));

		}
		
		
		previousPlayer.put(sender.getName(), pName);
		previousResults.put(sender.getName(), listOfFiles);
		
	
		return true;
	}

	
	
	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, "ps.search", "/%s search <player>", cmd);
	}

	public boolean hasValues() {
		return false;
	}
	
}
