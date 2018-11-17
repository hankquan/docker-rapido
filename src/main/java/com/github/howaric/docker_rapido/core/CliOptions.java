package com.github.howaric.docker_rapido.core;

import com.beust.jcommander.Parameter;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.google.common.base.Strings;

public class CliOptions {

    @Parameter(names = {"--help", "-h"}, description = "Get command usage")
    @JsonIgnore
    private boolean help;

    @Parameter(names = {"--official"}, description = "Declare --official if this is an official deployment")
    private boolean isDeclareOfficial;

    @Parameter(names = {"--clean", "-c"}, description = "Clean the services which has build param")
    private boolean isClean;

    @Parameter(names = {"--force-clean", "-fc"}, description = "Clean all the services")
    private boolean isForceClean;

    @Parameter(names = {
            "--rollback"}, description = "Declare that it is a deployment for rollback, rapido will skip image building and use the specific image-tag to accomplish this deployment")
    private boolean isRollback;

    @Parameter(names = {"--node-label", "-nl"}, description = "Deployment will constraint on the servers with this label")
    private String nodeLabel;

    @Parameter(names = {"--tag-latest", "-tl"}, description = "Add latest tag when build and push image")
    private boolean isTagLatest;

    @Parameter(names = {"--tag", "-it"}, description = "Tag of image, for example: 1.12.5")
    private String tag;

    @Parameter(names = {"--template", "-t"}, description = "Required: Full local path of rapido template file")
    private String templateFilePath;

    @Parameter(names = {"-logdir"}, description = "Log folder where logs will be created, current folder as default")
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

    public boolean isRollback() {
        return isRollback;
    }

    public void setRollback(boolean isRollback) {
        this.isRollback = isRollback;
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

    public String getNodeLabel() {
        return nodeLabel;
    }

    public void setNodeLabel(String nodeLabel) {
        this.nodeLabel = nodeLabel;
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

    @Override
    public String toString() {
        final StringBuilder sb = new StringBuilder("CliOptions{");
        sb.append("help=").append(help);
        sb.append(", isDeclareOfficial=").append(isDeclareOfficial);
        sb.append(", isClean=").append(isClean);
        sb.append(", isForceClean=").append(isForceClean);
        sb.append(", isRollback=").append(isRollback);
        sb.append(", nodeLabel='").append(nodeLabel).append('\'');
        sb.append(", isTagLatest=").append(isTagLatest);
        sb.append(", tag='").append(tag).append('\'');
        sb.append(", templateFilePath='").append(templateFilePath).append('\'');
        sb.append(", logDir='").append(logDir).append('\'');
        sb.append('}');
        return sb.toString();
    }
}
