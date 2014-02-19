package com.cyprias.PlayerSnapshot.commands;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.command.CommandSender;
import com.cyprias.PlayerSnapshot.Plugin;
import com.cyprias.PlayerSnapshot.command.Command;
import com.cyprias.PlayerSnapshot.command.CommandAccess;
import com.cyprias.PlayerSnapshot.configuration.Config;
import com.cyprias.PlayerSnapshot.utils.ChatUtils;

public class RenameCommand implements Command {
	public void listCommands(CommandSender sender, List<String> list) {
		if (sender.hasPermission("ps.rename"))
			list.add("/%s rename - Rename a snapshop.");
	}
	
	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) throws IOException {
		if (!Plugin.checkPermission(sender, "ps.rename"))
			return false;
		
		if (!SearchCommand.previousResults.containsKey(sender.getName())){
			ChatUtils.send(sender, Config.getString("messages.UseSearch"));
			return true;
		}
		
		if (args.length <= 1){
			ChatUtils.send(sender, "/%s rename <id> <newName>");
			return true;
		}
		
		int index;
		if (Plugin.isInt(args[0])) {
			index = Integer.parseInt(args[0]);
		} else {
			ChatUtils.send(sender, Config.getString("messages.InvalidIndex", args[0]));
			return true;
		}

		String toName = Plugin.getFinalArg(args, 1);
		
		// Player who owns the snapshot.
		String pName = SearchCommand.previousPlayer.get(sender.getName());

		// Our search list. 
		File[] listOfFiles = SearchCommand.previousResults.get(sender.getName());
		if (index > listOfFiles.length){
			ChatUtils.send(sender, Config.getString("messages.InvalidIndex", args[0]));
			return true;
		}
		
		// File we're changing.
		File f = listOfFiles[index];
		
		//Current name for later.
		String oldName = f.getName();
		
		// Player's dat folder.
		String playerDir = Plugin.getInstance().getDataFolder() + File.separator + "dats" + File.separator + pName;		

		// New location for the file.
		String newDir = playerDir + File.separator + toName + ".dat";
		//Logger.info("path: " + f.getPath());
		//Logger.info("playerDir: " + playerDir);
		//Logger.info("newDir: " + newDir);
		
		// Create the file object.
		File newFile = new File(newDir);
		// Rename the old file. 
		f.renameTo(newFile);
		
		ChatUtils.send(sender, Config.getString("messages.SnapRenamed", oldName, newFile.getName()));
		

		
		return true;
	}
	
	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, "ps.rename", "/%s rename <id> [newName]", cmd);
	}

	public boolean hasValues() {
		return false;
	}

}