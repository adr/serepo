package ch.hsr.isf.serepo.relations.check;

import java.util.ArrayList;
import java.util.List;

import com.google.common.collect.ImmutableList;

import ch.hsr.isf.serepo.git.repository.log.GitCommitLog;

public class CheckResult {

  public static class Inconsistency {
    private String file;
    private String type;
    private String url;
    private String inconsistencyReason;
    private GitCommitLog commitLog;

    public Inconsistency(String file, String type, String url, String inconsistencyReason,
        GitCommitLog commitLog) {
      this.file = file;
      this.type = type;
      this.url = url;
      this.inconsistencyReason = inconsistencyReason;
      this.commitLog = commitLog;
    }

    public String getFile() {
      return file;
    }

    public String getType() {
      return type;
    }

    public String getUrl() {
      return url;
    }

    public String getInconsistencyReason() {
      return inconsistencyReason;
    }

    public GitCommitLog getCommitLog() {
      return commitLog;
    }

  }

  private List<Inconsistency> inconsistencies = new ArrayList<>();

  public CheckResult() {}

  public void addInconsistency(Inconsistency inconsistency) {
    inconsistencies.add(inconsistency);
  }

  public boolean hasInconsistencies() {
    return !inconsistencies.isEmpty();
  }

  public List<Inconsistency> getInconsistencies() {
    return ImmutableList.copyOf(inconsistencies);
  }

}
