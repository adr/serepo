package ch.hsr.isf.serepo.commons;

import java.io.IOException;
import java.io.InputStream;

import org.apache.tika.detect.Detector;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.mime.MediaType;
import org.apache.tika.mime.MimeTypeException;
import org.apache.tika.parser.AutoDetectParser;

public class MimeTypes {

	private MimeTypes() {
	}

	public static String get(InputStream is) throws IOException {

		AutoDetectParser parser = new AutoDetectParser();
		Detector detector = parser.getDetector();
		Metadata md = new Metadata();
		MediaType mediaType = detector.detect(is, md);

		return mediaType.toString();

	}

	public static String getFileExtension(String mimeType) throws MimeTypeException {
		org.apache.tika.mime.MimeTypes allTypes = org.apache.tika.mime.MimeTypes.getDefaultMimeTypes();
		return allTypes.forName(mimeType).getExtension();
	}

}
