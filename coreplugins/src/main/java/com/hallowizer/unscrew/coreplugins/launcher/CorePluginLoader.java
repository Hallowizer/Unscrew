package com.hallowizer.unscrew.coreplugins.launcher;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.hallowizer.unscrew.api.CorePlugin;

import cpw.mods.fml.relauncher.RelaunchClassLoader;
import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CorePluginLoader {
	private final String[] builtinCorePlugins = {
		"com.hallowizer.unscrew.coreplugins.UnscrewCorePlugin"
	};
	private final Yaml yaml = new Yaml();
	private final List<CorePluginWrapper> corePlugins = new ArrayList<>();
	
	@SneakyThrows
	public void launch(RelaunchClassLoader classLoader, File unscrewJarLocation) {
		classLoader.registerTransformer("com.hallowizer.unscrew.coreplugins.transformer.PatchingTransformer");
		
		for (String plugin : builtinCorePlugins)
			loadCorePlugin(classLoader, null, plugin, unscrewJarLocation);
		
		findCorePlugins(classLoader);
		corePlugins.sort((plugin1, plugin2) -> plugin1.getSortingIndex()-plugin2.getSortingIndex());
		corePlugins.forEach(CorePluginWrapper::invoke);
	}
	
	@SneakyThrows
	private void findCorePlugins(RelaunchClassLoader classLoader) {
		File pluginsDir = new File("plugins");
		if (!pluginsDir.exists())
			pluginsDir.mkdir();
		
		Preconditions.checkState(pluginsDir.isDirectory(), "The plugins folder was a file.");
		
		for (File file : pluginsDir.listFiles(file -> file.isFile() && file.getName().endsWith(".jar")))
			try (JarFile jar = new JarFile(file)) {
				JarEntry pluginYaml = jar.getJarEntry("plugin.yml");
				InputStream in = jar.getInputStream(pluginYaml);
				Map<String,Object> map = yaml.load(in);
				
				if (map.containsKey("depend")) {
					Iterable<?> dependencies = (Iterable<?>) map.get("depend");
					
					if (Iterables.contains(Iterables.transform(dependencies, Object::toString), "Unscrew")) {
						String name = map.get("name").toString();
						String main = map.get("main").toString();
						
						classLoader.addURL(file.toURI().toURL());
						loadCorePlugin(classLoader, name, main, file);
					}
				}
			}
	}
	
	@SuppressWarnings("unchecked")
	@SneakyThrows
	private void loadCorePlugin(RelaunchClassLoader classLoader, String name, String main, File location) {
		Class<? extends CorePlugin> clazz = (Class<? extends CorePlugin>) Class.forName(main, true, classLoader);
		CorePlugin plugin = clazz.newInstance();
		
		if (plugin instanceof BuiltinCorePlugin)
			name = ((BuiltinCorePlugin) plugin).getName();
		
		corePlugins.add(new CorePluginWrapper(name, location, plugin));
	}
	
	public List<CorePluginWrapper> getCorePlugins() {
		return ImmutableList.copyOf(corePlugins);
	}
}
