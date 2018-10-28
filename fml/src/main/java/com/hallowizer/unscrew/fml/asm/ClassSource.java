package com.hallowizer.unscrew.fml.asm;

import java.io.IOException;

public interface ClassSource {
	public abstract byte[] getClassBytes(String name) throws IOException;
}
