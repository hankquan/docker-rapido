package com.github.howaric.docker_rapido.cli;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.CommaParameterSplitter;

public class CliOptions {

	@Parameter(names = { "--web-mode" })
	private boolean webMode;

	@Parameter(names = { "--official" }, description = "official release if true")
	private boolean isDeclareOfficial;

	@Parameter(names = { "--image-tag",
			"-it" }, description = "image tags: -it demo1:0.0.1,demo2:0.0.3", splitter = CommaParameterSplitter.class)
	private List<String> imageTag = new ArrayList<>();

	@Parameter(names = { "--template", "-t" }, description = "local path of rapido template file")
	private String templateFilePath;

	public boolean isWebMode() {
		return webMode;
	}

	public void setWebMode(boolean webMode) {
		this.webMode = webMode;
	}

	public boolean isDeclareOfficial() {
		return isDeclareOfficial;
	}

	public void setDeclareOfficial(boolean isDeclareOfficial) {
		this.isDeclareOfficial = isDeclareOfficial;
	}

	public String getTemplateFilePath() {
		return templateFilePath;
	}

	public void setTemplateFilePath(String templateFilePath) {
		this.templateFilePath = templateFilePath;
	}

	public List<String> getImageTag() {
		return imageTag;
	}

	public void setImageTag(List<String> imageTag) {
		this.imageTag = imageTag;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("RapidoCliOptions [webMode=").append(webMode).append(", isDeclareOfficial=")
				.append(isDeclareOfficial).append(", imageTag=").append(imageTag).append(", templateFilePath=")
				.append(templateFilePath).append("]");
		return builder.toString();
	}

}
