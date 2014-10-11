package com.cyprias.PlayerSnapshot.commands;

import java.util.List;

import org.bukkit.command.CommandSender;
import com.cyprias.PlayerSnapshot.Plugin;
import com.cyprias.PlayerSnapshot.command.Command;
import com.cyprias.PlayerSnapshot.command.CommandAccess;
import com.cyprias.PlayerSnapshot.utils.ChatUtils;


public class VersionCommand implements Command {
	public void listCommands(CommandSender sender, List<String> list) {
		if (sender.hasPermission("ps.version"))
			list.add("/%s version - Get the plugin version.");
	}

	public boolean execute(final CommandSender sender, org.bukkit.command.Command cmd, String[] args) {
		if (!Plugin.checkPermission(sender, "ps.version"))
			return false;

		ChatUtils.send(sender, "§7We're running version v§f" + Plugin.getInstance().getDescription().getVersion());

		return true;
	}

	public CommandAccess getAccess() {
		return CommandAccess.BOTH;
	}

	public void getCommands(CommandSender sender, org.bukkit.command.Command cmd) {
		ChatUtils.sendCommandHelp(sender, "ps.version", "/%s version - Get the plugin version.", cmd);
	}

	public boolean hasValues() {
		return false;
	}
}
