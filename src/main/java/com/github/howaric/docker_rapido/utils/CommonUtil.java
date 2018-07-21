package com.github.howaric.docker_rapido.utils;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtil {

	private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

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

}
