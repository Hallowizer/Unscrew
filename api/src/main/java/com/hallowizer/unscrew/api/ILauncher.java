package com.hallowizer.unscrew.api;

public interface ILauncher {
	public abstract void setContext(LauncherContext ctx);
	public abstract String getMainClass();
}
