package com.hallowizer.unscrew.coreplugins.transformer;

import com.hallowizer.unscrew.api.IClassTransformer;
import com.hallowizer.unscrew.api.TransformerException;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class PluginWrapperTransformer implements cpw.mods.fml.relauncher.IClassTransformer {
	private final String plugin;
	private final IClassTransformer target;
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		try {
			return target.transform(name, transformedName, bytes);
		} catch (TransformerException e) {
			throw e;
		} catch (Exception e) {
			throw new TransformerException(String.format("An exception occurred in a core plugin's transformer. This occurred in core plugin %s, transformer %s (%s)", plugin, target.getClass().getSimpleName(), target.getClass().getName()), e);
		}
	}
}
