package com.hallowizer.unscrew.coreplugins.launcher;

import java.io.File;
import java.lang.reflect.Field;
import java.net.URI;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import cpw.mods.fml.relauncher.RelaunchClassLoader;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class UnscrewLauncher {
	@SneakyThrows
	public void main(String[] args) {
		ClassLoader oldClassLoader = UnscrewLauncher.class.getClassLoader();
		RelaunchClassLoader newClassLoader;
		
		if (oldClassLoader instanceof URLClassLoader)
			newClassLoader = new RelaunchClassLoader(((URLClassLoader) oldClassLoader).getURLs());
		else if (oldClassLoader.getClass().getSuperclass().getName().equals("jdk.internal.loader.BuiltinClassLoader"))
			newClassLoader = new RelaunchClassLoader(findUrls(oldClassLoader));
		else
			throw new UnsupportedOperationException("Unsupported environment. Unknown class loader " + oldClassLoader.getClass().getName());
		
		URI jarUri = UnscrewLauncher.class.getProtectionDomain().getCodeSource().getLocation().toURI();
		File jarLocation = new File(jarUri);
		
		addExclusions(newClassLoader);
		CorePluginLoader.launch(newClassLoader, jarLocation);
	}
	
	@SuppressWarnings("unchecked")
	@SneakyThrows
	private URL[] findUrls(ClassLoader classLoader) {
		Class<?> builtinClassLoader = Class.forName("jdk.internal.loader.BuiltinClassLoader");
		Field moduleMapField = builtinClassLoader.getDeclaredField("packageToModule");
		moduleMapField.setAccessible(true);
		Map<String,?> packageToModule = (Map<String, ?>) moduleMapField.get(classLoader);
		Collection<?> modules = packageToModule.values();
		
		if (modules.isEmpty())
			return new URL[0];
		
		Class<?> loadedModule = modules.iterator().next().getClass();
		Field codeSourceURL = loadedModule.getDeclaredField("codeSourceURL");
		codeSourceURL.setAccessible(true);
		
		List<URL> urls = new ArrayList<>();
		for (Object module : modules)
			urls.add((URL) codeSourceURL.get(module));
		
		return urls.toArray(new URL[urls.size()]);
	}
	
	private void addExclusions(RelaunchClassLoader classLoader) {
		classLoader.addClassLoaderExclusion("com.hallowizer.unscrew.coreplugins.launcher.");
		classLoader.addClassLoaderExclusion("com.hallowizer.unscrew.api.");
		classLoader.addClassLoaderExclusion("net.minecraftforge.fml.relauncher.");
		
		classLoader.addTransformerExclusion("com.hallowizer.unscrew.coreplugins.transformer.");
		classLoader.addTransformerExclusion("com.hallowizer.unscrew.coreplugins.resource.");
		classLoader.addTransformerExclusion("net.minecraftforge.fml.common.asm.transformers.");
		classLoader.addTransformerExclusion("net.minecraftforge.fml.common.patcher.");
	}
}
