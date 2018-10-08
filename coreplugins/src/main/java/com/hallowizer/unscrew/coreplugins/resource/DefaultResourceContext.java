package com.hallowizer.unscrew.coreplugins.resource;

import java.io.OutputStream;

import com.hallowizer.unscrew.api.resource.Resource;
import com.hallowizer.unscrew.api.resource.ResourceContext;
import com.hallowizer.unscrew.api.resource.WritableResource;

import lombok.SneakyThrows;

public final class DefaultResourceContext implements ResourceContext {
	@Override
	public WritableResource newResource(String name) {
		return new DefaultWritableResource(name, null);
	}
	
	@Override
	@SneakyThrows
	public WritableResource copyResource(Resource resource) {
		WritableResource copy = new DefaultWritableResource(resource.getName(), resource.getOwner());
		OutputStream out = copy.asOutputStream();
		out.write(resource.asByteArray());
		return copy;
	}
}
