package com.hallowizer.unscrew.coreplugins.resource;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.util.Arrays;

import com.hallowizer.unscrew.api.resource.WritableResource;

public final class DefaultWritableResource extends DefaultResource implements WritableResource {
	private ByteArrayOutputStream out = new ByteArrayOutputStream();
	
	public DefaultWritableResource(String name, String owner) {
		super(name, owner, new byte[0]);
	}
	
	@Override
	public byte[] asByteArray() {
		byte[] data = out.toByteArray();
		return Arrays.copyOf(data, data.length);
	}
	
	@Override
	public OutputStream asOutputStream() {
		return out;
	}
	
	@Override
	public Writer asWriter() {
		return new OutputStreamWriter(out);
	}
	
	@Override
	public void clear() {
		out = new ByteArrayOutputStream();
	}
}
