package ch.hsr.isf.serepo.data.restinterface.commit;

import ch.hsr.isf.serepo.data.restinterface.common.User;

public class CreateCommit {

	private String message;
	private CommitMode mode;
	private User user;

	public CreateCommit() {
	}

	public String getMessage() {
		return message;
	}

	public CommitMode getMode() {
		return mode;
	}

	public void setMode(CommitMode mode) {
		this.mode = mode;
	}

	public User getUser() {
		return user;
	}

	public void setUser(User user) {
		this.user = user;
	}

	public void setMessage(String message) {
		this.message = message;
	}

}
