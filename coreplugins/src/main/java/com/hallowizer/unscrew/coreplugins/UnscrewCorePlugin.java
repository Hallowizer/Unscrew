package com.hallowizer.unscrew.coreplugins;

import java.io.File;
import java.util.List;

import com.hallowizer.unscrew.api.CorePlugin;
import com.hallowizer.unscrew.api.IClassTransformer;
import com.hallowizer.unscrew.api.resource.IResourceTransformer;
import com.hallowizer.unscrew.coreplugins.launcher.BuiltinCorePlugin;
import com.hallowizer.unscrew.coreplugins.resource.ResourceManager;
import com.hallowizer.unscrew.coreplugins.transformer.ResourceClassTransformer;
import com.hallowizer.unscrew.coreplugins.transformer.ResourceReadingTransformer;

import lombok.Getter;

public final class UnscrewCorePlugin extends BuiltinCorePlugin {
	@Getter
	private static File jarLocation;
	
	@Override
	public String getName() {
		return "Unscrew";
	}
	
	@Override
	public IClassTransformer[] getClassTransformers() {
		return new IClassTransformer[] {
			new ResourceClassTransformer(),
			new ResourceReadingTransformer()
		};
	}
	
	@Override
	public void injectData(List<CorePlugin> corePlugins, File location) {
		jarLocation = location;
		
		for (CorePlugin plugin : corePlugins)
			for (IResourceTransformer transformer : plugin.getResourceTransformers())
				ResourceManager.registerTransformer(transformer);
	}
}
