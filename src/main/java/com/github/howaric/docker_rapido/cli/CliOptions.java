package com.github.howaric.docker_rapido.cli;

import java.util.ArrayList;
import java.util.List;

import com.beust.jcommander.Parameter;
import com.beust.jcommander.converters.CommaParameterSplitter;
import com.google.common.base.Strings;

public class CliOptions {

	@Parameter(names = { "--help", "-h" }, description = "Get command usage")
	private boolean help;

	@Parameter(names = { "--web-mode" }, description = "Start docker-rapido as a rest service")
	private boolean webMode;

	@Parameter(names = { "--official" }, description = "Declare --official if this is an official deployment")
	private boolean isDeclareOfficial;

	@Parameter(names = { "--image-tag",
			"-it" }, description = "Image tags: 0.0.1-snapshot, specify service name if there are more than one as app1:0.0.1,app2:0.0.3", splitter = CommaParameterSplitter.class)
	private List<String> imageTag = new ArrayList<>();

	@Parameter(names = { "--template", "-t" }, description = "Required: Full local path of rapido template file")
	private String templateFilePath;

	@Parameter(names = { "-logdir" }, description = "Log folder where logs will be created, current folder as default")
	private String logDir;

	public boolean needShowUsage() {
		if (Strings.isNullOrEmpty(templateFilePath)) {
			System.err.println("ERROR: Template file can not be empty\n");
			return true;
		}
		return false;
	}

	public boolean isHelp() {
		return help;
	}

	public void setHelp(boolean help) {
		this.help = help;
	}

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
		StringBuilder builder = new StringBuilder();
		builder.append("CliOptions [help=").append(help).append(", webMode=").append(webMode)
				.append(", isDeclareOfficial=").append(isDeclareOfficial).append(", imageTag=").append(imageTag)
				.append(", templateFilePath=").append(templateFilePath).append(", logDir=").append(logDir).append("]");
		return builder.toString();
	}

}
