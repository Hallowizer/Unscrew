package com.hallowizer.unscrew.patcher;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import com.google.common.io.Files;
import com.hallowizer.unscrew.fml.classloading.ClassSource;

import lombok.RequiredArgsConstructor;

@RequiredArgsConstructor
public final class DirectoryClassSource implements ClassSource {
	private final File root;
	
	@Override
	public byte[] getClassBytes(String name) throws IOException {
		try {
			File file = new File(root, name.replace('.', '/') + ".class");
			return Files.asByteSource(file).read();
		} catch (FileNotFoundException e) {
			return null;
		}
	}
}
