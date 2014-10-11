package com.cyprias.PlayerSnapshot.commands;

import java.util.List;

import org.bukkit.command.CommandSender;

import com.cyprias.PlayerSnapshot.Plugin;
import com.cyprias.PlayerSnapshot.command.Command;
import com.cyprias.PlayerSnapshot.command.CommandAccess;
import com.cyprias.PlayerSnapshot.utils.ChatUtils;

public class ReloadCommand implements Command {
	public void listCommands(CommandSender sender, List<String> list) {
		if (sender.hasPermission("ps.reload"))
			list.add("/%s reload - Reload the plugin.");
	}

	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if (!Plugin.checkPermission(sender, "ps.reload"))
			return false;
		Plugin instance = Plugin.getInstance();
		
		instance.reloadConfig();

		ChatUtils.send(sender, "Plugin reloaded.");

		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, "ps.reload", "/%s reload - Reload the plugin.", cmd);
	}

	public boolean hasValues() {
		return false;
	}
}


