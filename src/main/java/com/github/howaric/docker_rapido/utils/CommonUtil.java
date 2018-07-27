package com.github.howaric.docker_rapido.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Date;
import java.util.List;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.util.ResourceUtils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

public class CommonUtil {

    private static Logger logger = LoggerFactory.getLogger(CommonUtil.class);

    public static File readTemplateFile(String path) {
        try {
            File file = ResourceUtils.getFile(path);
            return file;
        } catch (Exception e) {
            logger.error("File doesn't exist: {}", path);
            e.printStackTrace();
        }
        return null;
    }

    public static List<String> readResourcesFileToList(String resourcesFile) {
        List<String> result = new ArrayList<>();
        String line = "";
        InputStream inputStream = CommonUtil.getResourcesFileInputStream(resourcesFile);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            while ((line = bufferedReader.readLine()) != null) {
                result.add(line);
            }
        } catch (Exception e) {
            try {
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return result;
    }
    
    public static String readResourcesFileToString(String resourcesFile) {
        StringBuilder result = new StringBuilder();
        String line = "";
        InputStream inputStream = CommonUtil.getResourcesFileInputStream(resourcesFile);
        InputStreamReader inputStreamReader = new InputStreamReader(inputStream);
        BufferedReader bufferedReader = new BufferedReader(inputStreamReader);
        try {
            while ((line = bufferedReader.readLine()) != null) {
                result.append(line);
                result.append("\n");
            }
        } catch (Exception e) {
            try {
                bufferedReader.close();
                inputStreamReader.close();
                inputStream.close();
            } catch (IOException e1) {
                e1.printStackTrace();
            }
        }
        return result.toString();
    }

    public static InputStream getResourcesFileInputStream(String resourcesRelativePath) {
        String jarPath = CommonUtil.class.getProtectionDomain().getCodeSource().getLocation().getPath();
        InputStream inputStream = null;
        try {
            URL url = new URL("jar:" + jarPath + resourcesRelativePath);
            inputStream = url.openStream();
        } catch (MalformedURLException e) {
            inputStream = CommonUtil.class.getClass().getResourceAsStream("/" + resourcesRelativePath);
        } catch (IOException e) {
            logger.error("Resource file {} get stream failed", resourcesRelativePath);
            e.printStackTrace();
        }
        return inputStream;
    }

    public static void sleep(int time) {
        try {
            Thread.sleep(time);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

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

    public static boolean hasElement(Collection<?> collection) {
        if (collection != null && !collection.isEmpty()) {
            return true;
        }
        return false;
    }

    public static boolean hasElement(Map<?, ?> map) {
        if (map != null && !map.isEmpty()) {
            return true;
        }
        return false;
    }

    public static String getTimeStamp() {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyyMMddHHmmss");
        return sdf.format(new Date());
    }
}
