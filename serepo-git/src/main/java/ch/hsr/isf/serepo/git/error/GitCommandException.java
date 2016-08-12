package ch.hsr.isf.serepo.git.error;

public class GitCommandException extends Exception {

  private static final long serialVersionUID = 8507534141662258588L;

  private String revstr;
  
  public GitCommandException(String message) {
    super(message);
  }
  
  public GitCommandException(String message, String revstr) {
    super(message);
    this.revstr = revstr;
  }

  public String getRevstr() {
    return revstr;
  }

}
