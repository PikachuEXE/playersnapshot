package com.cyprias.PlayerSnapshot.command;

import java.util.List;

import org.bukkit.command.CommandSender;

public interface Listable {

	void listCommands(CommandSender sender, List<String> commands);

}