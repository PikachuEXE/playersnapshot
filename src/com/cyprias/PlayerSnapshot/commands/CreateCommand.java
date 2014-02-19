package com.cyprias.PlayerSnapshot.commands;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.cyprias.PlayerSnapshot.Logger;
import com.cyprias.PlayerSnapshot.Plugin;
import com.cyprias.PlayerSnapshot.command.Command;
import com.cyprias.PlayerSnapshot.command.CommandAccess;
import com.cyprias.PlayerSnapshot.configuration.Config;
import com.cyprias.PlayerSnapshot.utils.ChatUtils;

public class CreateCommand implements Command {
	public void listCommands(CommandSender sender, List<String> list) {
		if (sender.hasPermission("ps.create"))
			list.add("/%s create - Create a snapshop.");
	}

	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if (!Plugin.checkPermission(sender, "ps.create"))
			return false;

		
		
		
		if (args.length  > 0){
			//Logger.info("0: " + args[0]);
			
			Player p = Plugin.getInstance().getServer().getPlayer(args[0]);
			
			if (p != null){
				try {
					SimpleDateFormat ft = new SimpleDateFormat ("yyyyMMdd.hhmmss"); 
					String date = ft.format(new Date());
					
					String snapName = "manual-"+date+"-"+Config.getString("properties.default-lifetime"); 
					
					if (args.length  > 1)
						snapName = Plugin.getFinalArg(args, 1);
					
					File f = Plugin.BackupPlayer(p, snapName);
					
					
					if (f != null){
						ChatUtils.send(sender, Config.getString("messages.CreatedSnapshot", snapName, p.getName()));
						return true;
					}
				} catch (IOException e) {e.printStackTrace();
			
				}
				ChatUtils.send(sender, Config.getString("messages.SnapshotFailed"));
				return true;
				
				
				
			}
			
			
		}

		ChatUtils.send(sender, "/%s create <player> [snapName]");
		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, "ps.create", "/%s create <player>", cmd);
	}

	public boolean hasValues() {
		return false;
	}
}
