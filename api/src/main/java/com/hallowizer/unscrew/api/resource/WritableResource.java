package com.hallowizer.unscrew.api.resource;

import java.io.OutputStream;
import java.io.Writer;

public interface WritableResource extends Resource {
	public abstract OutputStream asOutputStream();
	public abstract Writer asWriter();
	
	public abstract void clear();
}
