package com.cyprias.PlayerSnapshot.configuration;

import java.io.IOException;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;

import com.cyprias.PlayerSnapshot.Logger;
import com.cyprias.PlayerSnapshot.Plugin;

public class Config {
	public static boolean getBoolean(String property) {
		return Plugin.getInstance().getConfig().getBoolean(property);
	}

	public static String getString(String property) {
		return Plugin.getInstance().getConfig().getString(property);
	}

	public static Integer getInt(String property) {
		return Plugin.getInstance().getConfig().getInt(property);
	}

	public static String getColouredString(String property) {
		return getString(property).replaceAll("(?i)&([a-k0-9])", "\u00A7$1");
	}
	
	public static String getString(String property, Object... args) {
		return String.format(getString(property), args);
	}
	
	
	public static  ConfigurationSection getConfigurationSection(String property) {
		return Plugin.getInstance().getConfig().getConfigurationSection(property);
	}
	
	public static void checkForMissingProperties() throws IOException, InvalidConfigurationException {
		YML diskConfig = new YML(Plugin.getInstance().getDataFolder(), "config.yml");
		YML defaultConfig = new YML(Plugin.getInstance().getResource("config.yml"));

		for (String property : defaultConfig.getKeys(true)) {
			if (!diskConfig.contains(property))
				Logger.warning(property + " is missing from your config.yml, using default.");
		}

	}
}
