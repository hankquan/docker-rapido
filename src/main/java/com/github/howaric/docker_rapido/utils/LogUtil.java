package com.github.howaric.docker_rapido.utils;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class LogUtil {

    private static Logger logger = LoggerFactory.getLogger(LogUtil.class);

    private static final int LINE_TOTAL_LENGTH = 100;
    private static final int BOX_MARGIN = 40;

    private static final String STAR = "*";
    private static final String BLANK = " ";
    private static final String NEXT_ROW = "\n";
    private static final String COLON = ":";

    public static void main(String[] args) {
        successfulExit();
    }

    public static void printInCentreWithStar(String title) {
        int length = title.length();
        int margin = (LINE_TOTAL_LENGTH - length) / 2;
        StringBuilder line = new StringBuilder();
        for (int i = 1; i < margin; i++) {
            line.append(STAR);
        }
        line.append(BLANK);
        line.append(title);
        line.append(BLANK);
        for (int i = 1; i < margin; i++) {
            line.append(STAR);
        }
        if (length % 2 != 0) {
            line.append(STAR);
        }
        logger.info(line.toString());
    }

    public static String fillMapInBox(Map<String, String> fillMap) {
        StringBuilder result = new StringBuilder();
        int maxLeft = 0;
        int maxRight = 0;
        for (Map.Entry<String, String> each : fillMap.entrySet()) {
            int leftWidth = each.getKey().length();
            int rightWidth = each.getValue().length();
            if (leftWidth > maxLeft) {
                maxLeft = leftWidth;
            }
            if (rightWidth > maxRight) {
                maxRight = rightWidth;
            }
        }
        int boxWidth = maxLeft + maxRight + 8;
        for (int i = 0; i < boxWidth; i++) {
            result.append(STAR);
        }
        result.append(NEXT_ROW);
        for (Map.Entry<String, String> each : fillMap.entrySet()) {
            String key = each.getKey();
            String value = each.getValue();
            result.append(STAR);
            result.append(BLANK);
            result.append(BLANK);
            result.append(key);
            result.append(COLON);
            int patch = boxWidth - 7 - key.length() - value.length();
            for (int k = 0; k < patch; k++) {
                result.append(BLANK);
            }
            result.append(value);
            result.append(BLANK);
            result.append(BLANK);
            result.append(STAR);
            result.append(NEXT_ROW);
        }
        for (int i = 0; i < boxWidth; i++) {
            result.append(STAR);
        }
        return result.toString();
    }

    public static void printLinesInBox(List<String> lines) {
        printLinesInBox(lines, null);
    }

    public static void printLinesInBox(List<String> lines, Integer marginForMaxLine) {
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
        printLinesInBox(Arrays.asList("Task successfully finished.", "Thank you for using docker-rapido, have a nice day!"));
    }

    public static void printEmptyLine() {
        logger.info(BLANK);
    }

}
