package com.hallowizer.unscrew.coreplugins.launcher;

import java.io.File;
import java.util.stream.Collectors;

import com.hallowizer.unscrew.api.CorePlugin;
import com.hallowizer.unscrew.api.IClassNameTransformer;
import com.hallowizer.unscrew.api.IClassTransformer;
import com.hallowizer.unscrew.coreplugins.transformer.PluginWrapperTransformer;
import com.hallowizer.unscrew.coreplugins.transformer.RenamingTransformer;

import cpw.mods.fml.relauncher.RelaunchClassLoader;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class CorePluginWrapper {
	private final String name;
	private final File location;
	private final CorePlugin instance;
	
	public int getSortingIndex() {
		return instance.getSortingIndex();
	}
	
	public void invoke() {
		instance.injectData(CorePluginLoader.getCorePlugins().stream().map(wrapper -> wrapper.instance).collect(Collectors.toList()), location);
		
		RelaunchClassLoader classLoader = (RelaunchClassLoader) instance.getClass().getClassLoader();
		
		for (IClassTransformer transformer : instance.getClassTransformers()) {
			classLoader.registerTransformer(new PluginWrapperTransformer(name, transformer));
			
			if (transformer instanceof IClassNameTransformer)
				RenamingTransformer.addTransformer((IClassNameTransformer) transformer);
		}
	}
}
