package com.hallowizer.unscrew.coreplugins.launcher;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.yaml.snakeyaml.Yaml;

import com.google.common.base.Preconditions;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.Iterables;
import com.hallowizer.modwrapper.api.ConfigurableClassLoader;
import com.hallowizer.unscrew.api.CorePlugin;
import com.hallowizer.unscrew.api.ILauncher;
import com.hallowizer.unscrew.coreplugins.transformer.PatchingTransformer;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class CorePluginLoader {
	private final String[] builtinCorePlugins = {
		"com.hallowizer.unscrew.coreplugins.UnscrewCorePlugin",
		"com.hallowizer.unscrew.coreplugins.McpCorePlugin"
	};
	private final Yaml yaml = new Yaml();
	private final List<CorePluginWrapper> corePlugins = new ArrayList<>();
	private final Map<File,Map<String,Object>> corePluginDescriptions = new HashMap<>();
	private final Map<File,CorePluginWrapper> corePluginMap = new HashMap<>();
	private DefaultLauncherContext launcher;
	
	@SneakyThrows
	public void setup(ConfigurableClassLoader classLoader, File unscrewJarLocation) {
		classLoader.registerTransformer(new PatchingTransformer());
		
		launcher = new DefaultLauncherContext(classLoader, new SpigotLauncher(), null);
		
		for (String plugin : builtinCorePlugins)
			loadCorePlugin(classLoader, null, plugin, unscrewJarLocation, plugin.endsWith("UnscrewCorePlugin") ? ImmutableList.of("ModCoderPack") : ImmutableList.of());
		
		findCorePlugins(classLoader);
		corePlugins.forEach(wrapper -> wrapper.invoke(classLoader));
	}
	
	@SneakyThrows
	private void findCorePlugins(ConfigurableClassLoader classLoader) {
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
						corePluginDescriptions.put(file, map);
						
						List<String> depends = new ArrayList<String>(Arrays.asList(Iterables.toArray(Iterables.transform(dependencies, Object::toString), String.class)));
						depends.remove("Unscrew");
						depends.addAll(Arrays.asList(Iterables.toArray(Iterables.transform((Iterable<?>) map.get("softdepend"), Object::toString), String.class)));
						
						loadCorePlugin(classLoader, name, main, file, depends);
					}
				}
			}
	}
	
	@SuppressWarnings("unchecked")
	@SneakyThrows
	private void loadCorePlugin(ConfigurableClassLoader classLoader, String name, String main, File location, List<String> depends) {
		Class<? extends CorePlugin> clazz = (Class<? extends CorePlugin>) Class.forName(main, true, classLoader);
		CorePlugin plugin = clazz.newInstance();
		
		if (plugin instanceof BuiltinCorePlugin)
			name = ((BuiltinCorePlugin) plugin).getName();
		
		CorePluginWrapper wrapper = new CorePluginWrapper(name, location, plugin, depends);
		corePlugins.add(wrapper);
		corePluginMap.put(location, wrapper);
	}
	
	public List<CorePluginWrapper> getCorePlugins() {
		return ImmutableList.copyOf(corePlugins);
	}
	
	public void addLauncher(ILauncher newLauncher) {
		launcher = new DefaultLauncherContext(launcher.getClassLoader(), newLauncher, launcher);
	}
	
	public void launch(String[] args) {
		launcher.launch(args);
	}
	
	public boolean isCorePlugin(File file) {
		return corePluginDescriptions.containsKey(file);
	}
	
	public Map<String,Object> getDescription(File file) {
		return corePluginDescriptions.get(file);
	}
	
	public Object getBukkitPlugin(File file) {
		return corePluginMap.get(file).getPlugin();
	}
}
