package com.github.howaric.docker_rapido.test;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.github.howaric.docker_rapido.utils.YamlUtil;
import com.github.howaric.docker_rapido.yaml_model.RapidoTemplate;

public class TestCilent {

	public static void main(String[] args) throws IOException {

		/**
		 * 1. java -jar docker-rapido.jar --official --image-tag 0.1.1 --template ./template.yaml
		 * 
		 * 2. curl -F "dockerfile=@Dockerfile-demo" -F "template=@template.yml"
		 * http://127.0.0.1:8000/rapido/release?image-tag=0.3.2
		 * 
		 */
		File file = ResourceUtils.getFile("classpath:template3.yml");
		//System.out.println(FileUtils.readFileToString(file));
		RapidoTemplate template = YamlUtil.getObj(file, RapidoTemplate.class);
		ObjectMapper mapper = new ObjectMapper();
		String writeValueAsString = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(template);
		System.out.println(writeValueAsString);
	}

}
