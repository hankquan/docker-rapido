package com.github.howaric.docker_rapido.utils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.Collection;
import java.util.Date;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtil {

	private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

	public static File readTemplateFile(String path) {
		try {
			File file = ResourceUtils.getFile(path);
			return file;
		} catch (Exception e) {
			logger.error("File doesn't exist: {}", path);
			e.printStackTrace();
		}
		return null;
	}

	public static File readResourcesFile(String path) {
		return null;
	}

	public static void sleep(int time) {
		try {
			Thread.sleep(time);
		} catch (InterruptedException e) {
			e.printStackTrace();
		}
	}

	public static String prettyJson(Object bean) {
		ObjectMapper mapper = new ObjectMapper();
		try {
			return mapper.writerWithDefaultPrettyPrinter().writeValueAsString(bean);
		} catch (JsonProcessingException e) {
			logger.error("parse bean to pretty json failed: " + e.getMessage());
			e.printStackTrace();
		}
		return null;
	}

	public static boolean hasElement(Collection<?> collection) {
		if (collection != null && !collection.isEmpty()) {
			return true;
		}
		return false;
	}

	public static boolean hasElement(Map<?, ?> map) {
		if (map != null && !map.isEmpty()) {
			return true;
		}
		return false;
	}

	public static String getTimeStamp() {
		SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
		return sdf.format(new Date());
	}
}
