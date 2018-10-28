package com.hallowizer.unscrew.api;

import java.io.File;
import java.util.List;

import org.bukkit.plugin.java.JavaPlugin;

import com.hallowizer.modwrapper.api.IClassTransformer;
import com.hallowizer.unscrew.api.resource.IResourceTransformer;

public abstract class CorePlugin {
	public IClassTransformer[] getClassTransformers() {
		return new IClassTransformer[0];
	}
	
	public IResourceTransformer[] getResourceTransformers() {
		return new IResourceTransformer[0];
	}
	
	public void injectData(List<CorePlugin> corePlugins, File location) {
		// NOOP
	}
	
	public ILauncher getLauncher() {
		return null;
	}
	
	public JavaPlugin getBukkitPlugin() {
		return null;
	}
}
