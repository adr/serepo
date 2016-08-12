package ch.hsr.isf.serepo.commons;

import java.io.File;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Paths;

public class Uri {

	private Uri() {
	}

	public static URI of(URI base, String... pathSegments) throws URISyntaxException {
		return of(base.toString(), pathSegments);
	}

	public static URI of(String base, String... pathSegments) throws URISyntaxException {
		StringBuilder sb = new StringBuilder();
		for (String pathSegment : pathSegments) {
			sb.append("/").append(pathSegment);
		}
		return new URI(null, null, String.format("%s%s", base, sb), null).normalize();
	}

	public static File toFile(URI base, String... pathSegments) throws URISyntaxException {
		return Paths.get(of(base, pathSegments)).toFile();
	}

}
