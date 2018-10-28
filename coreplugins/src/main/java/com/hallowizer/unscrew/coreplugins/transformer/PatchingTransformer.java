package com.hallowizer.unscrew.coreplugins.transformer;

import com.hallowizer.modwrapper.api.IClassTransformer;

import net.minecraftforge.fml.common.patcher.ClassPatchManager;

public final class PatchingTransformer implements IClassTransformer {
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		return ClassPatchManager.INSTANCE.applyPatch(name, transformedName, bytes);
	}
}
