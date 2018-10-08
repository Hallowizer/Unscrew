package com.hallowizer.unscrew.api;

public interface IClassTransformer {
	public abstract byte[] transform(String name, String transformedName, byte[] classData);
}
