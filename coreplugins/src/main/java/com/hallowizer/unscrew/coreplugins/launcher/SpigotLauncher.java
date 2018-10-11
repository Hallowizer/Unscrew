package com.hallowizer.unscrew.coreplugins.launcher;

import com.google.common.base.Preconditions;
import com.hallowizer.unscrew.api.ILauncher;
import com.hallowizer.unscrew.api.LauncherContext;

public final class SpigotLauncher implements ILauncher {
	@Override
	public void setContext(LauncherContext ctx) {
		Preconditions.checkArgument(ctx == null, "SpigotLauncher is being used by a plugin.");
	}
	
	@Override
	public String getMainClass() {
		return "org.bukkit.craftbukkit.Main";
	}
}
