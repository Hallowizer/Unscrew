package com.hallowizer.unscrew.coreplugins.launcher;

import java.lang.reflect.Method;

import com.hallowizer.unscrew.api.ILauncher;
import com.hallowizer.unscrew.api.LauncherContext;

import cpw.mods.fml.relauncher.RelaunchClassLoader;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;

@RequiredArgsConstructor
public final class DefaultLauncherContext implements LauncherContext {
	@Getter
	private final RelaunchClassLoader classLoader;
	private final ILauncher launcher;
	private final DefaultLauncherContext next;
	
	@Override
	@SneakyThrows
	public void launch(String[] args) {
		launcher.setContext(next);
		
		String name = launcher.getMainClass();
		Class<?> clazz = Class.forName(name, true, classLoader);
		Method main = clazz.getDeclaredMethod("main", String[].class);
		main.invoke(null, (Object) args);
	}
}
