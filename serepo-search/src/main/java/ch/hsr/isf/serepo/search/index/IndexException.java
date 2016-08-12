package ch.hsr.isf.serepo.search.index;

public class IndexException extends Exception {

	private static final long serialVersionUID = -4976071187857722214L;

	public IndexException() {
	}

	public IndexException(String message) {
		super(message);
	}

	public IndexException(Throwable cause) {
		super(cause);
	}

	public IndexException(String message, Throwable cause) {
		super(message, cause);
	}
}
