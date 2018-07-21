package com.github.howaric.docker_rapido.core;

import java.io.File;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.github.howaric.docker_rapido.utils.CommonUtil;
import com.github.howaric.docker_rapido.utils.ValidatorUtil;
import com.github.howaric.docker_rapido.utils.YamlUtil;
import com.github.howaric.docker_rapido.yaml_model.RapidoTemplate;

public class RapidoEngine {

	private static Logger logger = LoggerFactory.getLogger(RapidoEngine.class);

	private File templateFile;
	private List<String> imageTags;

	private RapidoTemplate rapidoTemplate;

	public RapidoEngine(File templateFile, List<String> imageTags) {
		super();
		this.templateFile = templateFile;
		this.imageTags = imageTags;
	}

	public void startRapido() {
		// 1 validate rapidoTemplate
		RapidoTemplate template = YamlUtil.getObj(templateFile, RapidoTemplate.class);
		Map<String, StringBuffer> validate = ValidatorUtil.validate(template);
		if (validate != null) {
			logger.error("Yaml template validation failed: \n" + CommonUtil.prettyJson(validate));
			return;
		}
		
		
		// 2 create rapidoJob
		// 3 offer rapidoJob to thread pool

	}

}
