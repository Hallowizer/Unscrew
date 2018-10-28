package com.hallowizer.unscrew.coreplugins.launcher;

import java.io.File;
import java.util.List;
import java.util.stream.Collectors;

import com.hallowizer.modwrapper.api.ConfigurableClassLoader;
import com.hallowizer.modwrapper.api.IClassTransformer;
import com.hallowizer.unscrew.api.CorePlugin;
import com.hallowizer.unscrew.api.ILauncher;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class CorePluginWrapper {
	@Getter
	private final String name;
	private final File location;
	private final CorePlugin instance;
	private final List<String> depends;
	
	public Object getPlugin() {
		return instance.getBukkitPlugin();
	}
	
	public void invoke(ConfigurableClassLoader classLoader) {
		instance.injectData(CorePluginLoader.getCorePlugins().stream().map(wrapper -> wrapper.instance).collect(Collectors.toList()), location);
		
		IClassTransformer[] transformers = instance.getClassTransformers();
		if (transformers != null)
			for (IClassTransformer transformer : instance.getClassTransformers())
				classLoader.registerTransformer(transformer);
		
		ILauncher launcher = instance.getLauncher();
		if (launcher != null)
			CorePluginLoader.addLauncher(launcher);
	}
}
