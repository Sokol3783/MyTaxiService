package org.example;

import java.util.Properties;

public class PropertiesManager {
  private static final String PATH_PROPERTIES = "/WEB-INF/app.properties";

  private static final String PATH_SCRIPT ="/WEB-INF/db-create.sql";
  public static Properties properties;

  public static String getStringFromProperties(String key) {
    return String.valueOf(getProperties().getProperty(key));
  }

  public static Integer getIntegerFromProperties(String key) {

    return Integer.valueOf(getProperties().getProperty(key));
  }

  public static Boolean getBooleanFromProperties(String key) {
    return Boolean.valueOf(getProperties().getProperty(key));
  }

  public  static Properties getProperties() {
    if (properties == null) {
      properties = new Properties();
    }
    return properties;
  }

  public static String getPathProperties() {
    return PATH_PROPERTIES;
  }

  public static String getPathScript() {
    return PATH_SCRIPT;
  }
}