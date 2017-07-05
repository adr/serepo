package ch.hsr.isf.serepo.rest.resources.commits;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;
import java.util.Date;

import ch.hsr.isf.serepo.commons.Uri;
import ch.hsr.isf.serepo.data.restinterface.commit.Commit;
import ch.hsr.isf.serepo.data.restinterface.commit.CommitContainer;
import ch.hsr.isf.serepo.data.restinterface.common.Link;
import ch.hsr.isf.serepo.data.restinterface.common.User;
import ch.hsr.isf.serepo.git.error.GitCommandException;
import ch.hsr.isf.serepo.git.repository.GitRepository;
import ch.hsr.isf.serepo.git.repository.GitRepositoryBuilder;
import ch.hsr.isf.serepo.git.repository.log.GitCommitLog;
import ch.hsr.isf.serepo.rest.resources.Resource;

public class Commits {

  private Commits() {}

  public static CommitContainer container(URI baseUri, File repositoriesDir, String repositoryName)
      throws IOException, URISyntaxException, GitCommandException {

    CommitContainer commitContainer = new CommitContainer();
    commitContainer.setId(baseUri);
    commitContainer.setRepository(repositoryName);
    commitContainer.getLinks()
                   .add(new Link("self", baseUri.toString()));
    commitContainer.getLinks()
                   .add(new Link(Resource.HATEOAS_PREFIX + "create_commit", baseUri.toString()));

    File repositoryDir = Paths.get(repositoriesDir.getAbsolutePath(), repositoryName + ".git")
                              .toFile();

    Date lastUpdate = null;
    try (GitRepository git = GitRepositoryBuilder.open(repositoryDir)) {
      for (GitCommitLog commitLog : git.log()) {
        Commit commit = commit(baseUri, repositoryName, commitLog);
        commitContainer.getCommits()
                       .add(commit);
        if (lastUpdate == null || lastUpdate.before(commit.getWhen())) {
          lastUpdate = commit.getWhen();
        }
      }
    }
    commitContainer.setUpdated(lastUpdate);

    return commitContainer;

  }

  public static Commit commit(URI baseUri, String repositoryName, GitCommitLog commitLog)
      throws URISyntaxException {

    Commit commit = new Commit();
    URI id = Uri.of(baseUri, "repos", repositoryName, "commits", commitLog.getCommitId());
    commit.setId(id);
    commit.setCommitId(commitLog.getCommitId());
    commit.setWhen(commitLog.getWhen());
    commit.setShortMessage(commitLog.getShortMessage());
    commit.setFullMessage(commitLog.getFullMessage());
    User author = new User(commitLog.getAuthor()
                                    .getName(),
        commitLog.getAuthor()
                 .getEmail());
    commit.setAuthor(author);
    commit.getLinks()
          .add(new Link("self", id.toString()));
    commit.getLinks()
          .add(new Link(Resource.HATEOAS_PREFIX + "show_seitems", Uri.of(id, "seitems")
                                           .toString()));

    return commit;

  }

  public static Commit commit(URI baseUri, File repositoriesDir, String repositoryName,
      String commitId) throws URISyntaxException, IOException, GitCommandException {

    File repositoryDir = Paths.get(repositoriesDir.getAbsolutePath(), repositoryName + ".git")
                              .toFile();

    try (GitRepository git = GitRepositoryBuilder.open(repositoryDir)) {
      GitCommitLog commitLog = git.log(commitId);
      return commit(baseUri, repositoryName, commitLog);
    }
  }

}
