package com.hallowizer.unscrew.coreplugins.resource;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.net.URLConnection;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import org.yaml.snakeyaml.Yaml;

import com.google.common.io.ByteStreams;
import com.hallowizer.modwrapper.api.ConfigurableClassLoader;
import com.hallowizer.unscrew.api.resource.IResourceTransformer;
import com.hallowizer.unscrew.api.resource.Resource;
import com.hallowizer.unscrew.coreplugins.UnscrewCorePlugin;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class ResourceManager {
	private final List<IResourceTransformer> transformers = new ArrayList<>();
	private final Map<String,byte[]> resourceCache = new HashMap<>();
	private final DefaultResourceContext ctx = new DefaultResourceContext();
	private final Map<URL,String> urlMap = new HashMap<>();
	private ConfigurableClassLoader classLoader;
	private final Yaml yaml = new Yaml();
	
	public void init(ConfigurableClassLoader classLoader) {
		ResourceManager.classLoader = classLoader;
	}
	
	public void registerTransformer(IResourceTransformer transformer) {
		transformers.add(transformer);
	}
	
	public byte[] transformClassAsResource(String name, byte[] data) {
		return getTransformedResource("/" + name.replace('.', '/') + ".class", classLoader.currentCodeSource.get(), data);
	}
	
	public URL getResource(Class<?> clazz, String name) {
		return getResource(clazz.getClassLoader(), name);
	}
	
	public InputStream getResourceAsStream(Class<?> clazz, String name) {
		return getResourceAsStream(clazz.getClassLoader(), name);
	}
	
	public URL getResource(ClassLoader classLoader, String name) {
		URL url = classLoader.getResource(name);
		urlMap.put(url, name);
		return url;
	}
	
	@SneakyThrows
	public InputStream openStream(URL url) {
		return getInputStream(url.openConnection());
	}
	
	@SneakyThrows
	public InputStream getInputStream(URLConnection connection) {
		return urlMap.containsKey(connection.getURL()) ? new ByteArrayInputStream(getTransformedResource(urlMap.get(connection.getURL()), connection.getURL(), ByteStreams.toByteArray(connection.getInputStream()))) : connection.getInputStream();
	}
	
	@SneakyThrows
	public InputStream getResourceAsStream(ClassLoader classLoader, String name) {
		URL url = getResource(classLoader, name);
		return getResourceAsStream(classLoader, url, name);
	}
	
	@SneakyThrows
	private InputStream getResourceAsStream(ClassLoader classLoader, URL url, String name) {
		InputStream in = url.openStream();
		byte[] data = ByteStreams.toByteArray(in);
		byte[] transformed = getTransformedResource(name, url, data);
		return new ByteArrayInputStream(transformed);
	}
	
	private byte[] getTransformedResource(String name, URL url, byte[] data) {
		return resourceCache.computeIfAbsent(name, unused -> transformResource(name, url, data));
	}
	
	private byte[] transformResource(String name, URL url, byte[] data) {
		String owner = findOwner(url);
		Resource resource = new DefaultResource(name, owner, data);
		
		for (IResourceTransformer transformer : transformers) {
			resource = transformer.transform(ctx, resource);
			
			if (resource.getOwner() == null)
				resource = new DefaultResource(resource.getName(), owner, resource.asByteArray());
		}
		
		return resource.asByteArray();
	}
	
	@SneakyThrows
	private String findOwner(URL url) {
		File file = new File(url.toURI());
		
		if (file.getParentFile().getName().equals("plugins"))
			return findPluginName(file);
		
		return file.equals(UnscrewCorePlugin.getJarLocation()) ? "Unscrew" : "CraftBukkit";
	}
	
	@SneakyThrows
	private String findPluginName(File file) {
		try (JarFile jar = new JarFile(file)) {
			JarEntry pluginYaml = jar.getJarEntry("plugin.yml");
			InputStream in = jar.getInputStream(pluginYaml);
			Map<String,Object> map = yaml.load(in);
			
			return map.get("name").toString();
		}
	}
}
