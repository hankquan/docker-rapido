package com.github.howaric.docker_rapido.utils;

import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.constructor.Constructor;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;

public class YamlUtil {

    public static <T> T getObj(File yamlFile, Class<T> target) {
        Yaml yaml = new Yaml(new Constructor(target));
        try {
            return (T) yaml.load(new FileInputStream(yamlFile));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static <T> T getObj(String yamlFile, Class<T> target) {
        Yaml yaml = new Yaml(new Constructor(target));
        return (T) yaml.load(yamlFile);
    }

}
