package com.hallowizer.unscrew.api.resource;

public interface ResourceContext {
	public abstract WritableResource newResource(String name);
	public abstract WritableResource copyResource(Resource resource);
}
