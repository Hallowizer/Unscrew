package com.hallowizer.unscrew.patcher;

import java.io.File;
import java.io.IOException;

import com.hallowizer.unscrew.fml.classloading.ClassSource;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DirectoryClassSource implements ClassSource {
	private final File root;
	
	@Override
	public byte[] getClassBytes(String name) throws IOException {
		// TODO Auto-generated method stub
		return null;
	}
}
