package com.hallowizer.unscrew.coreplugins;

import com.hallowizer.unscrew.api.IClassTransformer;
import com.hallowizer.unscrew.coreplugins.launcher.BuiltinCorePlugin;

import net.minecraftforge.fml.common.asm.transformers.DeobfuscationTransformer;

public final class McpCorePlugin extends BuiltinCorePlugin {
	@Override
	public String getName() {
		return "ModCoderPack";
	}
	
	@Override
	public IClassTransformer[] getClassTransformers() {
		return new IClassTransformer[] {
			new DeobfuscationTransformer()
		};
	}
	
	@Override
	public int getSortingIndex() {
		return -1000;
	}
}
