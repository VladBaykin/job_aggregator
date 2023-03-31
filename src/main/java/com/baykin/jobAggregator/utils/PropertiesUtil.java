package com.baykin.jobAggregator.utils;

import java.io.InputStream;
import java.util.Properties;

public class PropertiesUtil {
    private Properties properties = new Properties();

    public String get(String key) {
        return this.properties.getProperty(key);
    }
    public Properties getProperties() {
        return properties;
    }

    public PropertiesUtil(String path) {
        loadProperties(path);
    }

    public void loadProperties(String path) {
        try (InputStream in = PropertiesUtil.class.getClassLoader().getResourceAsStream(path)) {
            this.properties.load(in);
        } catch (Exception e) {
            throw new IllegalStateException(e);
        }
    }
}
