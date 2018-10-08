package com.hallowizer.unscrew.coreplugins.resource;

import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.io.Reader;
import java.io.StringReader;
import java.util.Arrays;

import com.google.common.base.Charsets;
import com.hallowizer.unscrew.api.resource.Resource;

import lombok.Getter;
import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public class DefaultResource implements Resource {
	@Getter
	private final String name;
	@Getter
	private final String owner;
	private final byte[] data;
	
	@Override
	public byte[] asByteArray() {
		return Arrays.copyOf(data, data.length);
	}
	
	@Override
	public InputStream asInputStream() {
		return new ByteArrayInputStream(asByteArray());
	}
	
	@Override
	public String asString() {
		return new String(asByteArray(), Charsets.UTF_8);
	}
	
	@Override
	public Reader asReader() {
		return new StringReader(asString());
	}
}
