package ch.hsr.isf.serepo.client.webapp.model;

import java.io.File;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.vaadin.server.VaadinSession;

public class Settings {

  private static final Logger logger = LoggerFactory.getLogger(Settings.class);
  
  private String solrUrl;
  private String serepoUrl;
  
  private static final String SETTINGS_FILENAME = "serepo-webapp-config.json";
  
  public Settings() {
  }

  public static Settings getFromSession() {
    return (Settings) VaadinSession.getCurrent().getAttribute(Settings.class.getName());
  }
  
  public static Settings read() {
    ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
    try {
      
      File f = new File(SETTINGS_FILENAME);
      logger.info("Try to read settings from: " + f.getAbsolutePath());
      if (!f.isFile()) {
        f = new File(".." + File.separator + SETTINGS_FILENAME);
        logger.info("Not found! Try to read settings from: " + f.getAbsolutePath());
        if (!f.exists()) {
          logger.warn("Settings file not found! We use default values!");
          Settings settings = new Settings();
          settings.serepoUrl = "http://localhost:8080/serepo";
          settings.solrUrl = "http://localhost:8983/solr/serepo";
          return settings;
        }
      }
      logger.info("File found at: " + f.getAbsolutePath());
      logger.info("Reading settings...");
      return objectMapper.readValue(f, Settings.class);
      
    } catch (IOException e) {
      String message = String.format("Error while loading settings from file '%s'", SETTINGS_FILENAME);
      logger.error(message, e);
      throw new RuntimeException(message);
    }
  }

  public String getSolrUrl() {
    return solrUrl;
  }

  public String getSerepoUrl() {
    return serepoUrl;
  }
  
}
