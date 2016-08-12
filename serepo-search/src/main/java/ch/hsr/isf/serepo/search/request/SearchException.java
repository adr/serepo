package ch.hsr.isf.serepo.search.request;

public class SearchException extends Exception {

	private static final long serialVersionUID = -7558946346502651564L;

	public SearchException() {
	}

	public SearchException(String message) {
		super(message);
	}

	public SearchException(Throwable cause) {
		super(cause);
	}

	public SearchException(String message, Throwable cause) {
		super(message, cause);
	}

}
