package com.hallowizer.unscrew.fml.asm;

import org.objectweb.asm.commons.Remapper;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public final class BukkitDeobfuscationRemapper extends Remapper {
	@Getter
	private static final BukkitDeobfuscationRemapper instance = new BukkitDeobfuscationRemapper();
	
	@Override
	public String map(String typeName) {
		return BukkitObfuscationRemapper.getInstance().unmap(typeName);
	}
}
