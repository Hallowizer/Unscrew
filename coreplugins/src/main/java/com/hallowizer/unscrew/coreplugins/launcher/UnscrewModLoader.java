package com.hallowizer.unscrew.coreplugins.launcher;

import java.io.File;
import java.util.List;

import com.hallowizer.modwrapper.api.ConfigurableClassLoader;
import com.hallowizer.modwrapper.api.IModLoader;

import lombok.SneakyThrows;

public final class UnscrewModLoader implements IModLoader {
	@Override
	public void injectData(List<String> args, String version, File gameDir) {
		// NOOP
	}
	
	@Override
	@SneakyThrows
	public void configureClassLoader(ConfigurableClassLoader classLoader) {
		classLoader.addTransformerExclusion("com.hallowizer.unscrew.coreplugins.launcher.");
		classLoader.addTransformerExclusion("com.hallowizer.unscrew.api.");
		classLoader.addTransformerExclusion("com.hallowizer.unscrew.fml.asm.");
		classLoader.addTransformerExclusion("com.hallowizer.unscrew.coreplugins.transformer.");
		classLoader.addTransformerExclusion("com.hallowizer.unscrew.coreplugins.resource.");
		classLoader.addTransformerExclusion("net.minecraftforge.fml.common.asm.transformers.");
		classLoader.addTransformerExclusion("net.minecraftforge.fml.common.patcher.");
		classLoader.addTransformerExclusion("net.minecraftforge.fml.relauncher.");
		
		File jarLocation = new File(getClass().getProtectionDomain().getCodeSource().getLocation().toURI());
		CorePluginLoader.setup(classLoader, jarLocation);
	}
	
	@Override
	public String getMainClass() {
		// TODO Auto-generated method stub
		return null;
	}
}
