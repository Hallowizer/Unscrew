package com.hallowizer.unscrew.patcher;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;

import com.google.common.base.Preconditions;
import com.google.common.io.ByteStreams;
import com.google.common.io.Files;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLDeobfuscatingRemapper;
import net.minecraftforge.fml.common.asm.transformers.deobf.FMLRemappingAdapter;

@UtilityClass
public class UnpatchedSource {
	public void generate(String[] args) {
		if (args.length < 2) {
			System.err.println("Usage: Patcher unpatched <source> <target>");
			return;
		}
		
		File source = new File(args[0]);
		if (!source.exists()) {
			System.err.println("Source folder must exist.");
			return;
		} else if (!source.isDirectory()) {
			System.err.println("Source folder was not a folder.");
			return;
		}
		
		File target = new File(args[1]);
		if (target.exists()) {
			System.err.println("Target folder exists, delete and try again.");
			return;
		}
		
		generate(source, target);
	}
	
	@SneakyThrows
	public void generate(File source, File target) {
		if (!target.exists())
			target.mkdirs();
		
		Preconditions.checkArgument(source.isDirectory(), "Source must be a directory");
		Preconditions.checkArgument(target.isDirectory(), "Target must be a directory");
		
		File fernFlower = new File("fernflower.jar");
		if (!fernFlower.exists()) {
			fernFlower.createNewFile();
			
			try (FileOutputStream out = new FileOutputStream(fernFlower)) {
				InputStream in = UnpatchedSource.class.getResourceAsStream("/fernflower.jar");
				
				ByteStreams.copy(in, out);
			}
		}
		
		File deobf = deobfuscate(source);
		
		URLClassLoader classLoader = (URLClassLoader) UnpatchedSource.class.getClassLoader(); // Please don't use Java 9 here, it's a waste of time.
		Method addURL = URLClassLoader.class.getDeclaredMethod("addURL", URL.class);
		addURL.setAccessible(true);
		addURL.invoke(classLoader, fernFlower.toURI().toURL());
		
		Class<?> clazz = Class.forName("org.jetbrains.java.decompiler.main.decompiler.ConsoleDecompiler");
		Method main = clazz.getDeclaredMethod("main", String[].class);
		main.invoke(null, (Object) new String[] {
			"-dgs=1",
			"-hdc=0",
			"-rbr=0",
			"-asc=1",
			"-udv=0",
			deobf.getPath(),
			target.getPath()
		});
	}
	
	private File deobfuscate(File source) {
		FMLDeobfuscatingRemapper.INSTANCE.setup(null, new DirectoryClassSource(source), "1.12.2");
		
		File deobf = new File("deobf");
		if (deobf.exists()) {
			System.out.println("Deobf folder exists, deleting");
			deobf.delete();
		}
		
		deobf.mkdir();
		
		exploreDir(source, deobf);
		return deobf;
	}
	
	private void exploreDir(File dir, File target) {
		for (File file : dir.listFiles())
			if (file.isDirectory())
				exploreDir(file, target);
			else if (file.isFile())
				deobfFile(file, target);
			else
				throw new RuntimeException("File was neither a directory or file"); // What kind of environment are you running this in? Are you running this in /dev? Because that's a bad idea.
	}
	
	@SneakyThrows
	private void deobfFile(File file, File target) {
		System.out.println("Deobfuscating file " + file.getPath());
		
		ClassWriter cw = new ClassWriter(ClassWriter.COMPUTE_MAXS);
		FMLRemappingAdapter remapper = new FMLRemappingAdapter(cw);
		ClassReader cr;
		
		try (FileInputStream in = new FileInputStream(file)) {
			cr = new ClassReader(in);
		}
		
		System.out.println("Class name: " + cr.getClassName());
		
		cr.accept(remapper, ClassReader.EXPAND_FRAMES);
		byte[] bytes = cw.toByteArray();
		
		String name = cr.getClassName();
		String mappedName = FMLDeobfuscatingRemapper.INSTANCE.map(name);
		
		File toWrite = new File(target, mappedName + ".class");
		toWrite.getParentFile().mkdirs();
		toWrite.createNewFile();
		
		Files.asByteSink(toWrite).write(bytes);
	}
}
