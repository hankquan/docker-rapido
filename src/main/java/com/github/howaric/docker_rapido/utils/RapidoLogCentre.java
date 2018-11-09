package com.github.howaric.docker_rapido.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class RapidoLogCentre {

	private static Logger logger = LoggerFactory.getLogger(RapidoLogCentre.class);

	private static final int LINE_TOTAL_LENGTH = 100;
	private static final int BOX_MARGIN = 40;

	public static void main(String[] args) {
		successfulExit();
	}

	public static void printInCentreWithStar(String title) {
		int length = title.length();
		int margin = (LINE_TOTAL_LENGTH - length) / 2;
		StringBuilder line = new StringBuilder();
		for (int i = 1; i < margin; i++) {
			line.append("*");
		}
		line.append(" ");
		line.append(title);
		line.append(" ");
		for (int i = 1; i < margin; i++) {
			line.append("*");
		}
		if (length % 2 != 0) {
			line.append("*");
		}
		logger.info(line.toString());
	}

	public static void printLinsInBox(List<String> lines) {
		printLinsInBox(lines, null);
	}

	public static void printLinsInBox(List<String> lines, Integer marginForMaxLine) {
		if (marginForMaxLine == null) {
			marginForMaxLine = BOX_MARGIN;
		}
		int maxLength = -1;
		for (String line : lines) {
			int length = line.length();
			if (maxLength == -1) {
				maxLength = length;
			} else {
				if (length > maxLength) {
					maxLength = length;
				}
			}
		}

		int maxLine = maxLength + marginForMaxLine;
		List<String> box = new ArrayList<>();
		StringBuilder firstLine = new StringBuilder();
		if (maxLine % 2 != 0) {
			maxLine -= 1;
		}
		for (int i = 1; i <= maxLine; i++) {
			if (i == 1 || i == maxLine) {
				firstLine.append("+");
			} else {
				firstLine.append("-");
			}
		}
		box.add(firstLine.toString());

		for (String line : lines) {
			StringBuilder currentLine = new StringBuilder();
			int length = line.length();
			int margin = (maxLine - length) / 2;
			for (int i = 1; i < margin; i++) {
				if (i == 1) {
					currentLine.append("|");
				} else {
					currentLine.append(" ");
				}
			}
			currentLine.append(" ");
			currentLine.append(line);
			currentLine.append(" ");
			int rightMargin = maxLine - margin - 1 - length;
			for (int i = 1; i <= rightMargin; i++) {
				if (i == rightMargin) {
					currentLine.append("|");
				} else {
					currentLine.append(" ");
				}
			}
			box.add(currentLine.toString());
		}
		StringBuilder lastLine = new StringBuilder();
		for (int i = 1; i <= maxLine; i++) {
			if (i == 1 || i == maxLine) {
				lastLine.append("+");
			} else {
				lastLine.append("-");
			}
		}
		box.add(lastLine.toString());
		for (String line : box) {
			logger.info(line);
		}

	}

	public static void successfulExit() {
	    printEmptyLine();
		printLinsInBox(Arrays.asList("Task successfully finished.", "Thank you for using docker-rapido, have a nice day!"));
	}
	
	public static void printEmptyLine() {
	    logger.info("");
	}
	
}
