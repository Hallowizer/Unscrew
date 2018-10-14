package com.hallowizer.unscrew.fml.asm;

import java.io.InputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.objectweb.asm.commons.Remapper;

import com.google.common.base.Charsets;
import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.io.ByteSource;
import com.google.common.io.ByteStreams;
import com.google.common.io.CharSource;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.SneakyThrows;

@NoArgsConstructor(access=AccessLevel.PRIVATE)
public final class BukkitObfuscationRemapper extends Remapper {
	@Getter
	private static final BukkitObfuscationRemapper instance = new BukkitObfuscationRemapper();
	
	private BiMap<String,String> bukkitClassMap;
	
	private Map<String,Map<String,String>> bukkitFieldMaps;
	private Map<String,Map<String,String>> bukkitMethodMaps;
	
	@SneakyThrows
	public void setup(String version) {
		bukkitClassMap = HashBiMap.create();
        
        InputStream classSrg = getClass().getResourceAsStream("/bukkit/bukkit-" + version + "-cl.csrg");
        ByteSource classSource = ByteSource.wrap(ByteStreams.toByteArray(classSrg));
        CharSource classChars = classSource.asCharSource(Charsets.UTF_8);
        List<String> classList = classChars.readLines();
        
        for (String line : classList) {
        	String uncommented = line.split("#")[0];
        	if (uncommented.equals(""))
        		continue;
        	
        	String[] parts = uncommented.split(" ");
        	bukkitClassMap.put(parts[1], parts[0]);
        }
        
        bukkitFieldMaps = new HashMap<>();
        bukkitMethodMaps = new HashMap<>();
        
        InputStream memberSrg = getClass().getResourceAsStream("/bukkit/bukkit-" + version +"-members.csrg");
        ByteSource memberSource = ByteSource.wrap(ByteStreams.toByteArray(memberSrg));
        CharSource memberChars = memberSource.asCharSource(Charsets.UTF_8);
        List<String> memberList = memberChars.readLines();
        
        for (String line : memberList) {
        	String uncommented = line.split("#")[0];
        	if (uncommented.equals(""))
        		continue;
        	
        	String[] parts = uncommented.split(" ");
        	if (parts.length == 3)
        		bukkitFieldMaps.computeIfAbsent(parts[0], unused -> new HashMap<>()).put(parts[2], parts[1]);
        	else if (parts.length == 4)
        		bukkitMethodMaps.computeIfAbsent(parts[0], unused -> new HashMap<>()).put(parts[3] + parts[2], parts[1]);
        }
	}
	
	@Override
	public String map(String typeName) {
		return bukkitClassMap.containsKey(typeName) ? bukkitClassMap.get(typeName) : typeName;
	}
	
	public String unmap(String typeName) {
		return bukkitClassMap.containsValue(typeName) ? bukkitClassMap.inverse().get(typeName) : typeName;
	}
	
	@Override
	public String mapFieldName(String owner, String name, String descriptor) {
		owner = unmap(owner);
		descriptor = BukkitDeobfuscationRemapper.getInstance().mapDesc(descriptor);
		
		Map<String,String> fieldMap = bukkitFieldMaps.get(owner);
		return fieldMap != null && fieldMap.containsKey(name) ? fieldMap.get(name) : name;
	}
	
	@Override
	public String mapMethodName(String owner, String name, String descriptor) {
		owner = unmap(owner);
		descriptor = BukkitDeobfuscationRemapper.getInstance().mapMethodDesc(descriptor);
		
		Map<String,String> methodMap = bukkitMethodMaps.get(owner);
		return methodMap != null && methodMap.containsKey(name + descriptor) ? methodMap.get(name + descriptor) : name;
	}
}
