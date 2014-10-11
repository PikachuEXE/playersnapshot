package com.cyprias.PlayerSnapshot.commands;

import java.io.File;
import java.util.List;

import org.bukkit.command.CommandSender;

import com.cyprias.PlayerSnapshot.Plugin;
import com.cyprias.PlayerSnapshot.command.Command;
import com.cyprias.PlayerSnapshot.command.CommandAccess;
import com.cyprias.PlayerSnapshot.configuration.Config;
import com.cyprias.PlayerSnapshot.utils.ChatUtils;

public class DeleteCommand implements Command {

	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if (!Plugin.checkPermission(sender, "ps.delete"))
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
		
		String pName = SearchCommand.previousPlayer.get(sender.getName());
		File[] listOfFiles = SearchCommand.previousResults.get(sender.getName());
		
		String name = listOfFiles[index].getName();
		listOfFiles[index].delete();
		
		ChatUtils.send(sender, Config.getString("messages.SnapshotDeleted", pName, name));

		
		
		return true;
	}

	
	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, "ps.delete", "/%s delete <id>", cmd);
	}

	public boolean hasValues() {
		return false;
	}


	public void listCommands(CommandSender sender, List<String> list) {
		if (sender.hasPermission("ps.delete"))
			list.add("/%s delete - Delete a snapshop.");
	}
	
}
