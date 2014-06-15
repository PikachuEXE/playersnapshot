package com.cyprias.PlayerSnapshot.commands;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cyprias.PlayerSnapshot.Plugin;
import com.cyprias.PlayerSnapshot.command.Command;
import com.cyprias.PlayerSnapshot.command.CommandAccess;
import com.cyprias.PlayerSnapshot.configuration.Config;
import com.cyprias.PlayerSnapshot.utils.ChatUtils;

public class RestoreCommand implements Command {
	public void listCommands(CommandSender sender, List<String> list) {
		if (sender.hasPermission("ps.restore"))
			list.add("/%s restore - Restore a snapshop.");
	}

	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) throws Exception {
		if (!Plugin.checkPermission(sender, "ps.restore"))
			return false;
		
		if (!SearchCommand.previousResults.containsKey(sender.getName())){
			ChatUtils.send(sender, Config.getString("messages.UseSearch"));
			return true;
		}
		
		if (args.length == 0){
			ChatUtils.send(sender, Config.getString("messages.AddIndex"));
			return true;
		}
		
		int index;
		if (Plugin.isInt(args[0])) {
			index = Integer.parseInt(args[0]);
		} else {
			ChatUtils.send(sender, Config.getString("messages.InvalidIndex", args[0]));
			return true;
		}
		//index -= 1; //our table indexs start at 0.
		
		
		String pName = SearchCommand.previousPlayer.get(sender.getName());
		
		//Player p = SearchCommand.previousPlayer.get(sender.getName());
		
		File[] listOfFiles = SearchCommand.previousResults.get(sender.getName());
		if (index > listOfFiles.length){
			ChatUtils.error(sender, index + " is too high, wtf");
			return true;
		}
		
		File file = listOfFiles[index];

		
		
		
		//ChatUtils.send(sender, "getPath " + file.getPath());
		Player p = Plugin.getInstance().getServer().getPlayer(pName);
		if (p != null && p.isOnline()){
			offlineQueue.put(p.getName(), file);
			//ChatUtils.send(sender, Config.getString("messages.RestoreAfterLogoff", p.getName()));
			ChatUtils.send(sender, Config.getString("messages.RestoreAfterLogoff", file.getName(), p.getName()));
			return true;
		}
		
		
		try {
			Plugin.RestorePlayer(file, pName);
			ChatUtils.send(sender, "Inventory restored.");
			return true;
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		ChatUtils.send(sender, "Restore failed.");
		
		return true;
	}
	
	public static HashMap<String, File> offlineQueue = new HashMap<String, File>();
	
	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, "ps.restore", "/%s restore <id>", cmd);
	}

	public boolean hasValues() {
		return false;
	}
	
}
