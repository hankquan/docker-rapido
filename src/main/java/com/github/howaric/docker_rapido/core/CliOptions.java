package com.github.howaric.docker_rapido.core;

import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;
import org.apache.commons.lang.builder.ToStringBuilder;

import java.util.ArrayList;
import java.util.List;

public class CliOptions {

    @Parameter(names = { "--help", "-h" }, description = "Get command usage")
    @JsonIgnore
    private boolean help;

    @Parameter(names = { "--official" }, description = "Declare --official if this is an official deployment")
    private boolean isDeclareOfficial;

    @Parameter(names = { "--clean", "-c" }, description = "Clean the services which has build param")
    private boolean isClean;

    @Parameter(names = { "--force-clean", "-fc" }, description = "Clean all the services")
    private boolean isForceClean;

    @Parameter(names = { "--tag-latest", "-tl" }, description = "Add latest tag when build and push image")
    private boolean isTagLatest;

    @Parameter(names = { "--tag", "--image-tag", "-it" }, description = "Tag of image, for example: 1.12.5")
    private String tag;

    @Parameter(names = { "--template", "-t" }, description = "Required: Full local path of rapido template file")
    private String templateFilePath;

    @Parameter(names = { "--parameter", "-p" }, description = "Parameters to format @key@ in template file")
    private List<String> parameters = new ArrayList<>();

    @Parameter(names = { "-logdir" }, description = "Log folder where logs will be created, current folder as default")
    private String logDir = ".";

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

    public boolean isDeclareOfficial() {
        return isDeclareOfficial;
    }

    public void setDeclareOfficial(boolean isDeclareOfficial) {
        this.isDeclareOfficial = isDeclareOfficial;
    }

    public boolean isClean() {
        return isClean;
    }

    public void setClean(boolean isClean) {
        this.isClean = isClean;
    }

    public String getTemplateFilePath() {
        return templateFilePath;
    }

    public void setTemplateFilePath(String templateFilePath) {
        this.templateFilePath = templateFilePath;
    }

    public boolean isTagLatest() {
        return isTagLatest;
    }

    public void setTagLatest(boolean tagLatest) {
        isTagLatest = tagLatest;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public String getLogDir() {
        return logDir;
    }

    public void setLogDir(String logDir) {
        this.logDir = logDir;
    }

    public boolean isForceClean() {
        return isForceClean;
    }

    public void setForceClean(boolean forceClean) {
        isForceClean = forceClean;
    }

    public List<String> getParameters() {
        return parameters;
    }

    public void setParameters(List<String> parameters) {
        this.parameters = parameters;
    }

    @Override
    public String toString() {
        return new ToStringBuilder(this).append("help", help).append("isDeclareOfficial", isDeclareOfficial).append("isClean", isClean)
                .append("isForceClean", isForceClean).append("isTagLatest", isTagLatest).append("tag", tag)
                .append("templateFilePath", templateFilePath).append("parameters", parameters).append("logDir", logDir).toString();
    }

}
