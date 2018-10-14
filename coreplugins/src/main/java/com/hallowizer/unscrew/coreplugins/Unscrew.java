package com.hallowizer.unscrew.coreplugins;

import java.io.File;
import java.util.Map;

import org.bukkit.plugin.PluginDescriptionFile;
import org.bukkit.plugin.java.JavaPlugin;

import com.hallowizer.unscrew.coreplugins.launcher.CorePluginLoader;

import lombok.experimental.UtilityClass;

@UtilityClass
public class Unscrew {
	public PluginDescriptionFile getPluginDescription(File file) {
		Map<String,Object> meta = CorePluginLoader.getDescription(file);
		return new PluginDescriptionFile(meta);
	}
	
	public JavaPlugin loadPlugin(File file) {
		return (JavaPlugin) CorePluginLoader.getBukkitPlugin(file);
	}
}
