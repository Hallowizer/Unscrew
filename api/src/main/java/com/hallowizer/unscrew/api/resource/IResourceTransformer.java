package com.hallowizer.unscrew.api.resource;

public interface IResourceTransformer {
	public abstract Resource transform(ResourceContext ctx, Resource resource);
}
