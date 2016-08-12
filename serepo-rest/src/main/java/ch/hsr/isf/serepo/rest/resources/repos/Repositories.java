package ch.hsr.isf.serepo.rest.resources.repos;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.google.common.io.Files;

import ch.hsr.isf.serepo.commons.Uri;
import ch.hsr.isf.serepo.data.restinterface.common.Link;
import ch.hsr.isf.serepo.data.restinterface.common.User;
import ch.hsr.isf.serepo.data.restinterface.repository.CreateRepository;
import ch.hsr.isf.serepo.data.restinterface.repository.Repository;
import ch.hsr.isf.serepo.data.restinterface.repository.RepositoryContainer;
import ch.hsr.isf.serepo.git.error.GitCommandException;
import ch.hsr.isf.serepo.git.repository.GitAuthor;
import ch.hsr.isf.serepo.git.repository.GitRepository;
import ch.hsr.isf.serepo.git.repository.GitRepository.FileReader;
import ch.hsr.isf.serepo.git.repository.GitRepositoryBuilder;
import ch.hsr.isf.serepo.git.repository.file.GitFile;
import ch.hsr.isf.serepo.git.repository.log.GitCommitLog;

public class Repositories {

  private Repositories() {}

  public static RepositoryContainer container(URI baseUri, File repositoriesDir)
      throws GitCommandException, IOException, URISyntaxException {

    List<File> repositories = new ArrayList<>();

    Iterable<File> directories = Files.fileTreeTraverser()
                                      .children(repositoriesDir);
    for (File dir : directories) {
      if (dir.isDirectory() && dir.getName()
                                  .toLowerCase()
                                  .endsWith(".git")) {
        repositories.add(dir);
      }
    }

    RepositoryContainer repositoryContainer = new RepositoryContainer();
    repositoryContainer.setId(Uri.of(baseUri, "repos"));
    repositoryContainer.setTitle("Repositories");
    Date lastUpdate = null;
    for (File repositoryDir : repositories) {
      Repository repository = repository(repositoryContainer.getId(), repositoryDir);
      repositoryContainer.getRepositories()
                         .add(repository);
      if (lastUpdate == null || lastUpdate.before(repository.getUpdated())) {
        lastUpdate = repository.getUpdated();
      }
    }
    repositoryContainer.setUpdated(lastUpdate);
    repositoryContainer.getLinks()
                       .add(new Link("self", Uri.of(baseUri, "repos")
                                                .toString()));
    repositoryContainer.getLinks()
                       .add(new Link("create", Uri.of(baseUri, "repos")
                                                  .toString()));

    return repositoryContainer;

  }

  public static Repository repository(URI baseUri, File repositoryDir)
      throws GitCommandException, IOException, URISyntaxException {

    final Repository repository = new Repository();
    repository.setName(repositoryDir.getName()
                                    .replace(".git", ""));
    URI id = Uri.of(baseUri, "repos", repository.getName());
    repository.setId(id);

    try (GitRepository git = GitRepositoryBuilder.open(repositoryDir)) {
      List<GitCommitLog> logs = git.log();
      repository.setUpdated(logs.get(0)
                                .getWhen());

      Set<User> setUser = new HashSet<>();
      for (GitCommitLog log : logs) {
        User user = new User(log.getAuthor()
                                .getName(),
            log.getAuthor()
               .getEmail());
        setUser.add(user);
      }
      repository.setAuthors(new ArrayList<>(setUser));
      repository.setLastUpdateUser(repository.getAuthors()
                                             .get(0));

      git.readFilesByPath("master", "readme.md", new FileReader() {
        
        @Override
        public boolean read(GitFile gitFile) {
          try {
            repository.setDescription(new String(gitFile.getBytes(), StandardCharsets.UTF_8));
          } catch (IOException e) {
            // TODO what should we do here?
          }
          return false;
        }
      });
    }

    repository.getLinks()
              .add(new Link("self", id.toString()));
    repository.getLinks()
              .add(new Link("delete", id.toString()));
    repository.getLinks()
              .add(new Link("serepo_commits", Uri.of(id, "commits")
                                               .toString()));

    return repository;

  }

  public static Response create(CreateRepository repository, File destination)
      throws IOException, GitCommandException {

    File repositoryDir = Paths.get(destination.getAbsolutePath(), repository.getName() + ".git")
                              .toFile();
    if (repositoryDir.exists()) {
      String body = String.format("Repository '%s' already exists.", repository.getName());
      return Response.status(Status.CONFLICT)
                     .entity(body)
                     .type(MediaType.TEXT_PLAIN_TYPE)
                     .build();
    } else {
      GitAuthor gitAuthor = new GitAuthor(repository.getUser()
                                                    .getName(),
          repository.getUser()
                    .getEmail());
      GitRepositoryBuilder.create(destination, repository.getName(), repository.getDescription(),
          gitAuthor);
      return Response.status(Status.CREATED)
                     .build();
    }

  }

}
