package ch.hsr.isf.serepo.git.repository;

import java.io.File;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.lib.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.google.common.io.Files;

import ch.hsr.isf.serepo.commons.FileUtils;
import ch.hsr.isf.serepo.git.error.GitCommandException;

public class GitRepositoryBuilder {

  private static final Logger logger = LoggerFactory.getLogger(GitRepositoryBuilder.class);

  public GitRepositoryBuilder() {}

  public static GitRepository open(File gitDir) throws IOException {
    return new GitRepository(Git.open(gitDir));
  }

  public static void create(File gitDir, String name, String description, GitAuthor author)
      throws IOException, GitCommandException {

    File repositoryDir = Paths.get(gitDir.getAbsolutePath(), name + ".git")
                              .toFile();
    repositoryDir.mkdirs();
    try (Git git = Git.init()
                      .setDirectory(repositoryDir)
                      .setBare(true)
                      .call()) {

      setLongPaths(git);
      
      File cloneDir = java.nio.file.Files.createTempDirectory(gitDir.toPath(), name + "_")
                                         .toFile();
      cloneDir.mkdirs();
      try (Git gitClone = clone(repositoryDir.toURI()
                                             .toString(),
          cloneDir)) {
        File readmeFile = Paths.get(cloneDir.getAbsolutePath(), "readme.md")
                               .toFile();
        readmeFile.createNewFile();
        Files.write(description, readmeFile, StandardCharsets.UTF_8);
        gitClone.add()
                .addFilepattern(".")
                .call();
        gitClone.commit()
                .setMessage("initial commit.")
                .setAuthor(author.toPersonIdent())
                .call();
        gitClone.push()
                .setRemote(Constants.DEFAULT_REMOTE_NAME)
                .add("master")
                .call();
      } finally {
        FileUtils.delete(cloneDir);
      }

    } catch (IllegalStateException | GitAPIException e) {
      String message = String.format("The repository %s could not be created.", name);
      logger.error(message, e);
      throw new IOException(message, e);
    }

  }

  public static Git clone(String sourceUri, File destination) throws GitCommandException {
    try {
      Git git = Git.cloneRepository()
                   .setURI(sourceUri)
                   .setDirectory(destination)
                   .call();
      setLongPaths(git);
      return git;
    } catch (GitAPIException | IOException e) {
      String message = String.format("There was an error while cloning the repository '%s' to '%s'.", sourceUri, destination.getAbsolutePath());
      logger.error(message, e);
      throw new GitCommandException("There was an error while cloning the repository.");
    }
  }

  private static void setLongPaths(Git git) throws IOException {
    git.getRepository()
       .getConfig()
       .setBoolean("core", null, "longpaths", true); // we need to do that, because of MS Windows!
    git.getRepository()
       .getConfig()
       .save();
  }

}
