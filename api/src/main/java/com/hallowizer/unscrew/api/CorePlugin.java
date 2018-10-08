package com.hallowizer.unscrew.api;

import java.io.File;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

public abstract class CorePlugin {
	public IClassTransformer[] getClassTransformers() {
		return new IClassTransformer[0];
	}
	
	public void injectData(List<CorePlugin> corePlugins, File location) {
		// NOOP
	}
	
	public int getSortingIndex() {
		return 0;
	}
	
	public JavaPlugin getBukkitPlugin() {
		return null;
	}
}
