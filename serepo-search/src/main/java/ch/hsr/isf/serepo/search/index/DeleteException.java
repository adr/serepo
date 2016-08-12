package ch.hsr.isf.serepo.search.index;

public class DeleteException extends Exception {

  private static final long serialVersionUID = -6799460144638784585L;

  public DeleteException() {
  }

  public DeleteException(String message) {
    super(message);
  }

  public DeleteException(Throwable cause) {
    super(cause);
  }

  public DeleteException(String message, Throwable cause) {
    super(message, cause);
  }

}
