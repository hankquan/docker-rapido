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

	@Parameter(names = { "--log-dir", "-logdir" }, description = "log dir")
	private String logDir;

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

	public String getLogDir() {
		return logDir;
	}

	public void setLogDir(String logDir) {
		this.logDir = logDir;
	}

	@Override
	public String toString() {
		final StringBuilder sb = new StringBuilder("CliOptions{");
		sb.append("webMode=").append(webMode);
		sb.append(", isDeclareOfficial=").append(isDeclareOfficial);
		sb.append(", imageTag=").append(imageTag);
		sb.append(", templateFilePath='").append(templateFilePath).append('\'');
		sb.append(", logDir='").append(logDir).append('\'');
		sb.append('}');
		return sb.toString();
	}
}
