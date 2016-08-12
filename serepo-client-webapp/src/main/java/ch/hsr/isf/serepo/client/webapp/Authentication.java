package ch.hsr.isf.serepo.client.webapp;

import com.google.common.base.Strings;

import ch.hsr.isf.serepo.client.webapp.model.User;

public class Authentication {

	private Authentication() {
	}

	public static User authenticate(String username, String email) {
		// TODO authenticate user (e.g. OAuth)
		if (!Strings.isNullOrEmpty(username) && !Strings.isNullOrEmpty(email)) {
			return new User(username, email);
		}
		return null;
	}
	
}
