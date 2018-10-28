package com.hallowizer.unscrew.coreplugins.transformer;

import org.objectweb.asm.ClassReader;
import org.objectweb.asm.ClassWriter;
import org.objectweb.asm.Opcodes;
import org.objectweb.asm.tree.AbstractInsnNode;
import org.objectweb.asm.tree.ClassNode;
import org.objectweb.asm.tree.MethodInsnNode;
import org.objectweb.asm.tree.MethodNode;

import com.hallowizer.modwrapper.api.IClassTransformer;

public final class ResourceReadingTransformer implements IClassTransformer {
	@Override
	public byte[] transform(String name, String transformedName, byte[] classData) {
		ClassNode clazz = new ClassNode();
		new ClassReader(classData).accept(clazz, 0);
		
		for (MethodNode method : clazz.methods)
			for (AbstractInsnNode insn : (Iterable<AbstractInsnNode>) () -> method.instructions.iterator())
				if (insn instanceof MethodInsnNode) {
					MethodInsnNode cast = (MethodInsnNode) insn;
					
					if (cast.getOpcode() == Opcodes.INVOKEVIRTUAL) {
						if ((cast.owner.equals("java/lang/Class") || cast.owner.equals("java/lang/ClassLoader")) && (cast.name.equals("getResource") || cast.name.equals("getResourceAsStream"))) {
							cast.setOpcode(Opcodes.INVOKESTATIC);
							cast.owner = "com/hallowizer/unscrew/coreplugins/resource/ResourceManager";
							cast.desc = "(L" + cast.owner + ";" + cast.desc.substring(1);
						} else if (cast.owner.equals("java/net/URL") && cast.name.equals("openStream")) {
							cast.setOpcode(Opcodes.INVOKESTATIC);
							cast.owner = "com/hallowizer/unscrew/coreplugins/resource/ResourceManager";
							cast.desc = "(Ljava/net/URL;)Ljava/io/InputStream;";
						} else if (cast.owner.equals("java/net/URLConnection") && cast.name.equals("getInputStream")) {
							cast.setOpcode(Opcodes.INVOKESTATIC);
							cast.owner = "com/hallowizer/unscrew/coreplugins/resource/ResourceManager";
							cast.desc = "(Ljava/net/URLConnection;)Ljava/io/InputStream;";
						}
					}
				}
		
		ClassWriter cw = new ClassWriter(0);
		clazz.accept(cw);
		return cw.toByteArray();
	}
}
