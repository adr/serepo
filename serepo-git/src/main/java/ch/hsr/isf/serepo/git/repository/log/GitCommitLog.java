package ch.hsr.isf.serepo.git.repository.log;

import java.util.Date;

import ch.hsr.isf.serepo.git.repository.GitAuthor;

public class GitCommitLog {

  private String commitId;
  private GitAuthor author;
  private String shortMessage;
  private String fullMessage;
  private Date when;

  public GitCommitLog(String commitId, GitAuthor author, String shortMessage, String fullMessage,
      Date when) {
    this.commitId = commitId;
    this.author = author;
    this.shortMessage = shortMessage;
    this.fullMessage = fullMessage;
    this.when = when;
  }

  public String getCommitId() {
    return commitId;
  }

  public GitAuthor getAuthor() {
    return author;
  }

  public String getShortMessage() {
    return shortMessage;
  }

  public String getFullMessage() {
    return fullMessage;
  }

  public Date getWhen() {
    Date date = when;
    if (date != null) {
      date = new Date(when.getTime());
    }
    return date;
  }

}
