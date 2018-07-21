package com.github.howaric.docker_rapido.utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

public class YamlUtil {

	@SuppressWarnings("unchecked")
	public static <T> T getObj(File yamlFile, Class<T> target) {
		Yaml yaml = new Yaml(new Constructor(target));
		try {
			return (T) yaml.load(new FileInputStream(yamlFile));
		} catch (FileNotFoundException e) {
			e.printStackTrace();
		}
		return null;
	}

}
