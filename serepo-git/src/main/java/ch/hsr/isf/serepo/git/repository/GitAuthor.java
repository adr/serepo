package ch.hsr.isf.serepo.git.repository;

import org.eclipse.jgit.lib.PersonIdent;

public class GitAuthor {

  private String name;
  private String email;
  
  public GitAuthor() {
  }

  public GitAuthor(String name, String email) {
    this.name = name;
    this.email = email;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public PersonIdent toPersonIdent() {
    return new PersonIdent(name, email);
  }
  
}
