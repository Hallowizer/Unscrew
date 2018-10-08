package com.hallowizer.unscrew.coreplugins.transformer;

import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.tree.ClassNode;

import com.hallowizer.unscrew.api.IClassNameTransformer;

import cpw.mods.fml.relauncher.IClassTransformer;
import lombok.SneakyThrows;

public final class RenamingTransformer implements IClassTransformer, cpw.mods.fml.relauncher.IClassNameTransformer {
	private static List<IClassNameTransformer> renamingTransformers = new ArrayList<>();
	
	@SuppressWarnings("unchecked")
	@SneakyThrows
	public RenamingTransformer() {
		Class<?> clazz = Class.forName(getClass().getName(), true, getClass().getClassLoader().getClass().getClassLoader());
		Field renamingTransformers = clazz.getDeclaredField("renamingTransformers");
		renamingTransformers.setAccessible(true);
		RenamingTransformer.renamingTransformers = (List<IClassNameTransformer>) renamingTransformers.get(null);
	}
	
	public static void addTransformer(IClassNameTransformer transformer) {
		renamingTransformers.add(transformer);
	}
	
	@Override
	public byte[] transform(String name, String transformedName, byte[] bytes) {
		ClassNode clazz = new ClassNode();
		new ClassReader(bytes).accept(clazz, 0);
		
		clazz.name = transformedName;
		
		ClassWriter cw = new ClassWriter(0);
		clazz.accept(cw);
		return cw.toByteArray();
	}
	
	@Override
	public String remapClassName(String name) {
		for (IClassNameTransformer transformer : renamingTransformers)
			name = transformer.transformName(name);
		
		return name;
	}
	
	@Override
	public String unmapClassName(String name) {
		List<IClassNameTransformer> newList = new ArrayList<>();
		newList.addAll(renamingTransformers);
		Collections.reverse(newList);
		
		for (IClassNameTransformer transformer : newList)
			name = transformer.untransformName(name);
		
		return name;
	}
}
