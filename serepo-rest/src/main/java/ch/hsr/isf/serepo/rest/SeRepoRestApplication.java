package ch.hsr.isf.serepo.rest;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import javax.ws.rs.ApplicationPath;
import javax.ws.rs.core.Application;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.swagger.jaxrs.config.BeanConfig;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@ApplicationPath("/")
public class SeRepoRestApplication extends Application {

  private static final Logger logger = LoggerFactory.getLogger(SeRepoRestApplication.class);
  
  private final File repositoriesDir;
  private final File repositoriesTempWorkingDir;
  private final File globalRelationDefinitionFile;
  private final String solrUrl;

  private static class AppSettings {
    public String repositoriesDir;
    public String repositoriesTempWorkingDir;
    public String globalRelationDefinitionFile;
    public String solrUrl;
  }
  
  public SeRepoRestApplication() {
    
    ObjectMapper objectMapper = new ObjectMapper(new JsonFactory());
    try {
      
      File f = new File("serepo-config.json");
      logger.info("Try to read settings from: " + f.getAbsolutePath());
      if (!f.isFile()) {
        f = new File(".." + File.separator + "serepo-config.json");
        logger.info("Not found! Try to read settings from: " + f.getAbsolutePath());
      }
      AppSettings appSettings;
      if (f.isFile()) {
        logger.info("File found at: " + f.getAbsolutePath());
        logger.info("Reading settings...");
        appSettings =  objectMapper.readValue(f, AppSettings.class);
        this.repositoriesDir = new File(appSettings.repositoriesDir);
        this.repositoriesTempWorkingDir = new File(appSettings.repositoriesTempWorkingDir);
        this.globalRelationDefinitionFile = new File(appSettings.globalRelationDefinitionFile);
        this.solrUrl = appSettings.solrUrl;
      } else {
        logger.info("Applyding default settings...");
        Path tempDir = Files.createTempDirectory("serepo");
        logger.info("Dir: " + tempDir.toString());
        this.repositoriesDir = tempDir.resolve("repos").toFile();
        this.repositoriesTempWorkingDir = tempDir.resolve("tmp").toFile();
        this.globalRelationDefinitionFile = tempDir.resolve("relations.yml").toFile();
        this.solrUrl = "http://localhost:8983/solr/serepo";
      }

      createDirIfNecessary(this.repositoriesDir);
      createDirIfNecessary(this.repositoriesTempWorkingDir);
      // checkIfFileExist(globalRelationDefinitionFile);
      
      logger.info("Settings read. RESTful HTTP service up and running!");
      
    } catch (IOException e) {
      String message = "Error while loading settings from file 'serepo-config.json'";
      logger.error(message, e);
      throw new RuntimeException(message, e);
    }
    
    setupSwagger();
    
  }
  
  private void setupSwagger() {
    BeanConfig beanConfig = new BeanConfig();
    beanConfig.setVersion("1.0.0");
    beanConfig.setSchemes(new String[] {"http"});
    beanConfig.setHost("localhost:8080");
    beanConfig.setBasePath("/serepo");
    beanConfig.setTitle("SE-Repo RESTful HTTP API");
    beanConfig.setResourcePackage("ch.hsr.isf.serepo.rest.resources");
    beanConfig.setPrettyPrint(true);
    beanConfig.setScan(true);
  }

  private void createDirIfNecessary(File f) {
    if (!f.isDirectory()) {
      f.mkdirs();
    }
  }
  
  private void checkIfFileExist(File f) throws FileNotFoundException {
    if (!f.exists()) {
      throw new FileNotFoundException(f.getAbsolutePath());
    }
  }

  public File getRepositoriesDir() {
    return repositoriesDir;
  }

  public File getRepositoriesTempWorkingDir() {
    return repositoriesTempWorkingDir;
  }

  public File getGlobalRelationDefinitionFile() {
    return globalRelationDefinitionFile;
  }

  public String getSolrUrl() {
    return solrUrl;
  }

}
