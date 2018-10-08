package com.hallowizer.unscrew.coreplugins.transformer;

import com.hallowizer.unscrew.api.IClassTransformer;
import com.hallowizer.unscrew.coreplugins.resource.ResourceManager;

public final class ResourceClassTransformer implements IClassTransformer {
	@Override
	public byte[] transform(String name, String transformedName, byte[] classData) {
		return ResourceManager.transformClassAsResource(transformedName, classData);
	}
}
