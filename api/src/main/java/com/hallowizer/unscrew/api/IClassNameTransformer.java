package com.hallowizer.unscrew.api;

public interface IClassNameTransformer extends IClassTransformer {
	public abstract String transformName(String name);
	public abstract String untransformName(String name);
}
