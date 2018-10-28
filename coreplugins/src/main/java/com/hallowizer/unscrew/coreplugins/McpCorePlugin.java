package com.hallowizer.unscrew.coreplugins;

import java.io.File;
import java.util.List;

import com.hallowizer.unscrew.api.CorePlugin;
import com.hallowizer.unscrew.api.IClassTransformer;
import com.hallowizer.unscrew.coreplugins.launcher.BuiltinCorePlugin;

import cpw.mods.fml.relauncher.RelaunchClassLoader;
import net.minecraftforge.fml.common.asm.transformers.DeobfuscationTransformer;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;

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
	public void injectData(List<CorePlugin> corePlugins, File location) {
		FMLDeobfuscatingRemapper.INSTANCE.setup(null, (RelaunchClassLoader) getClass().getClassLoader(), "deobfuscation_data-1.12.2.lzma");
	}
}
