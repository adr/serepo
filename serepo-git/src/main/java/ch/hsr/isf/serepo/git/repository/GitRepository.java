package ch.hsr.isf.serepo.git.repository;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.LogCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.errors.IncorrectObjectTypeException;
import org.eclipse.jgit.errors.MissingObjectException;
import org.eclipse.jgit.errors.NoWorkTreeException;
import org.eclipse.jgit.errors.RevisionSyntaxException;
import org.eclipse.jgit.lib.Constants;
import org.eclipse.jgit.lib.ObjectId;
import org.eclipse.jgit.lib.ObjectLoader;
import org.eclipse.jgit.lib.ObjectStream;
import org.eclipse.jgit.lib.PersonIdent;
import org.eclipse.jgit.merge.MergeStrategy;
import org.eclipse.jgit.revwalk.RevCommit;
import org.eclipse.jgit.revwalk.RevWalk;
import org.eclipse.jgit.treewalk.TreeWalk;
import org.eclipse.jgit.treewalk.filter.PathFilter;
import org.eclipse.jgit.treewalk.filter.PathSuffixFilter;
import org.eclipse.jgit.treewalk.filter.TreeFilter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import ch.hsr.isf.serepo.git.error.GitCommandException;
import ch.hsr.isf.serepo.git.repository.file.GitFile;
import ch.hsr.isf.serepo.git.repository.log.GitCommitLog;

public class GitRepository implements AutoCloseable {

  private static final Logger logger = LoggerFactory.getLogger(GitRepository.class);

  private Git git;

  public GitRepository(Git git) {
    this.git = git;
  }

  public void add(String repositoryRelativePath) throws GitCommandException {
    try {
      git.add()
         .addFilepattern(repositoryRelativePath.replace("\\", "/"))
         .call();
    } catch (GitAPIException e) {
      String message = String.format("There was an error while adding file '%s' to the index",
          repositoryRelativePath);
      logger.error(message, e);
      throw new GitCommandException(message);
    }
  }

  public boolean hasUncomittedChanges() throws GitCommandException {
    try {
      return git.status()
                .call()
                .hasUncommittedChanges();
    } catch (NoWorkTreeException | GitAPIException e) {
      String message = String.format("There was an error while checking status on git repository.");
      logger.error(message, e);
      throw new GitCommandException(message);
    }
  }

  public String commit(String message, GitAuthor author) throws GitCommandException {
    try {
      return git.commit()
                .setAll(true)
                .setAllowEmpty(false)
                .setMessage(message)
                .setAuthor(author.toPersonIdent())
                .call()
                .getId()
                .name();
    } catch (GitAPIException e) {
      String errorMessage = String.format("There was an error while committing.");
      logger.error(errorMessage, e);
      throw new GitCommandException(errorMessage);
    }
  }

  public void checkout(String branch) throws GitCommandException {
    checkout(branch, false);
  }

  public void checkoutNew(String branch) throws GitCommandException {
    checkout(branch, true);
  }

  private void checkout(String branch, boolean createBranch) throws GitCommandException {
    try {
      git.checkout()
         .setName(branch)
         .setCreateBranch(createBranch)
         .call();
    } catch (GitAPIException e) {
      String message = String.format("There was an error while checkout branch %s.", branch);
      logger.error(message, e);
      throw new GitCommandException(message);
    }
  }

  public void merge(String branch) throws GitCommandException {
    try {
      git.merge()
         .include(resolve(branch))
         .setStrategy(MergeStrategy.OURS) // TODO needed?
         .call();
    } catch (GitAPIException e) {
      String message = String.format("There was an error while merging branch %s.", branch);
      logger.error(message, e);
      throw new GitCommandException(message);
    }
  }

  public void push(String reference) throws GitCommandException {
    try {
      git.push()
         .setRemote(Constants.DEFAULT_REMOTE_NAME)
         .add(reference)
         .setPushTags()
         .call();
    } catch (GitAPIException e) {
      String message = String.format("There was an error while pushing %s", reference);
      logger.error(message, e);
      throw new GitCommandException(message);
    }
  }

  public interface FileReader {
    /**
     * This method is called for each file which meets the filter. Return <code>false</code> if you
     * don't longer want to be called by the FileReader.
     * 
     * @param gitFile
     * @return true if you want to be called for any other file. false if you want to abort the
     *         reading process.
     */
    boolean read(GitFile gitFile);
  }

  public void readFilesByPath(String revstr, String path, FileReader fileReader)
      throws GitCommandException {
    try {
      readFiles(revstr, PathFilter.create(path), fileReader);
    } catch (IOException e) {
      String message =
          String.format("An error occured while reading '%s' in commit '%s'.", path, revstr);
      logger.error(message, e);
      throw new GitCommandException(message, revstr);
    }
  }

  public void readFilesByExtension(String revstr, String fileExtensionFilter, FileReader fileReader)
      throws GitCommandException {
    try {
      readFiles(revstr, PathSuffixFilter.create(fileExtensionFilter), fileReader);
    } catch (IOException e) {
      String message =
          String.format("An error occured while reading files in commit '%s'.", revstr);
      logger.error(message, e);
      throw new GitCommandException(message, revstr);
    }
  }

  private void readFiles(String revstr, TreeFilter treeFilter, FileReader fileReader)
      throws GitCommandException, IOException {
    ObjectId commitId = resolve(revstr);
    try (RevWalk revWalk = new RevWalk(git.getRepository())) {
      try (TreeWalk treeWalk = new TreeWalk(git.getRepository())) {
        treeWalk.addTree(revWalk.parseCommit(commitId)
                                .getTree());
        treeWalk.setRecursive(true);
        treeWalk.setFilter(treeFilter);
        while (treeWalk.next()) {
          ObjectLoader objectLoader = treeWalk.getObjectReader()
                                              .open(treeWalk.getObjectId(0));
          ObjectStream stream = objectLoader.openStream();
          String fileName = treeWalk.getNameString();
          String filePath = treeWalk.getPathString();
          filePath = filePath.substring(0, filePath.length() - fileName.length());
          boolean readFurther = fileReader.read(new GitFile(fileName, filePath, stream));
          if (!readFurther) {
            break;
          }
        }
      }
    }
  }

  public List<GitCommitLog> log() throws GitCommandException {
    return log(Constants.MASTER, -1);
  }

  public GitCommitLog log(String revstr) throws GitCommandException {
    return log(revstr, 1).get(0);
  }

  public GitCommitLog logLatest(String revstrStartingFrom, String path) throws GitCommandException {
    try {
      Iterable<RevCommit> revCommits = git.log()
                                          .add(resolve(revstrStartingFrom))
                                          .addPath(path)
                                          .setMaxCount(1)
                                          .call();
      return log(revCommits).get(0);
    } catch (IOException | GitAPIException e) {
      String message = String.format("An error occured while loading log for file '%s'.", path);
      logger.error(message, e);
      throw new GitCommandException(message, revstrStartingFrom);
    }
  }

  /**
   * git log command.
   * 
   * @param revstr branch, commit, ...
   * @param count if < 0: all logs are returned.
   * @return
   * @throws GitCommandException
   */
  private List<GitCommitLog> log(String revstr, int count) throws GitCommandException {
    LogCommand logCommand = git.log();
    if (count >= 0) {
      logCommand.setMaxCount(count);
    }
    try {
      Iterable<RevCommit> revCommits = logCommand.add(resolve(revstr))
                                                 .call();
      return log(revCommits);
    } catch (GitAPIException | MissingObjectException | IncorrectObjectTypeException e) {
      String message = "An error occured while loading logs.";
      logger.error(message, e);
      throw new GitCommandException(message, revstr);
    }
  }

  private List<GitCommitLog> log(Iterable<RevCommit> revCommits) {
    List<GitCommitLog> commitLogs = new ArrayList<>();
    for (RevCommit revCommit : revCommits) {

      PersonIdent personIdent = revCommit.getAuthorIdent();
      GitAuthor author = new GitAuthor(personIdent.getName(), personIdent.getEmailAddress());
      Date when = new Date(personIdent.getWhen()
                                      .getTime());

      GitCommitLog gitCommitLog = new GitCommitLog(revCommit.getName(), author,
          revCommit.getShortMessage(), revCommit.getFullMessage(), when);
      commitLogs.add(gitCommitLog);

    }
    return commitLogs;
  }

  private ObjectId resolve(String revstr) throws GitCommandException {
    try {
      return git.getRepository()
                .resolve(revstr);
    } catch (RevisionSyntaxException e) {
      String message = String.format("The revision %s is malformed.", revstr);
      logger.error(message, e);
      throw new GitCommandException(message, revstr);
    } catch (IOException e) {
      String message = String.format("The revision %s could not be resolved.", revstr);
      logger.error(message, e);
      throw new GitCommandException(message, revstr);
    }
  }

  @Override
  public void close() {
    git.getRepository()
       .close();
    git.close();
  }

}
