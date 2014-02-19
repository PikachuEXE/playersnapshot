package com.cyprias.PlayerSnapshot.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cyprias.PlayerSnapshot.Plugin;
import com.cyprias.PlayerSnapshot.command.Command;
import com.cyprias.PlayerSnapshot.command.CommandAccess;
import com.cyprias.PlayerSnapshot.configuration.Config;
import com.cyprias.PlayerSnapshot.utils.ChatUtils;

public class RestoreToCommand implements Command {
	public void listCommands(CommandSender sender, List<String> list) {
		if (sender.hasPermission("ps.restoreto"))
			list.add("/%s restoreto - Restore a snapshop to someone.");
	}
	
	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if (!Plugin.checkPermission(sender, "ps.restoreto"))
			return false;
		
		if (!SearchCommand.previousResults.containsKey(sender.getName())){
			ChatUtils.send(sender, Config.getString("messages.UseSearch"));
			return true;
		}
		
		if (args.length == 0){
			ChatUtils.send(sender, "/%s restoreto <id> [playerName]");
			return true;
		}

		int index;
		if (Plugin.isInt(args[0])) {
			index = Integer.parseInt(args[0]);
		} else {
			ChatUtils.send(sender, Config.getString("messages.InvalidIndex", args[0]));
			return true;
		}
		
		String toName = sender.getName();
		if (args.length > 1){
			toName = args[1];
		}
		
		//index -= 1; //our table indexs start at 0.
		
		
		String pName = SearchCommand.previousPlayer.get(sender.getName());
		
		//Player p = SearchCommand.previousPlayer.get(sender.getName());
		
		File[] listOfFiles = SearchCommand.previousResults.get(sender.getName());
		if (index > listOfFiles.length){
			ChatUtils.send(sender, Config.getString("messages.InvalidIndex", args[0]));
			return true;
		}
		
		File file = listOfFiles[index];

		
		
		
		//ChatUtils.send(sender, "getPath " + file.getPath());
		Player p = Plugin.getInstance().getServer().getPlayer(pName);
		if (p != null && p.isOnline()){
			RestoreCommand.offlineQueue.put(toName, file);
			ChatUtils.send(sender, Config.getString("messages.RestoreAfterLogoff", file.getName(), toName));
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
		
		return true;
	}
	
	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, "ps.restoreto", "/%s restoreto <id> [playerName]", cmd);
	}

	public boolean hasValues() {
		return false;
	}

}
