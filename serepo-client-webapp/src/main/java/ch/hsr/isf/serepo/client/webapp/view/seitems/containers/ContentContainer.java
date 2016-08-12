package ch.hsr.isf.serepo.client.webapp.view.seitems.containers;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import javax.ws.rs.core.MediaType;

import com.vaadin.server.ExternalResource;
import com.vaadin.server.FileDownloader;
import com.vaadin.ui.BrowserFrame;
import com.vaadin.ui.Button;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Embedded;
import com.vaadin.ui.Image;
import com.vaadin.ui.Label;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

public class ContentContainer extends CustomComponent {

	private static final long serialVersionUID = 5998448307103483508L;

	private final Panel panel;
	private static final MediaType IMAGE_TYPE = MediaType.valueOf("image/*");
	private static final MediaType PDF_TYPE = MediaType.valueOf("application/pdf");

	public ContentContainer() {
		setSizeFull();
		panel = new Panel();
		panel.setSizeFull();
		setCompositionRoot(panel);
	}
	
	public void clearContent() {
		panel.setContent(new VerticalLayout());
	}
	
	public void setContent(final String src) {
		try {
			setContent(new URL(src));
		} catch (MalformedURLException e) {
			e.printStackTrace();
			Label label = new Label(String.format("There was an error with the resource!\n%s", e.getMessage()));
			label.addStyleName(ValoTheme.LABEL_FAILURE);
			panel.setContent(label);
		}
	}
	
	public void setContent(final URL src) {
		Component content = null;

		HttpURLConnection urlConnection = null;
		try {
			urlConnection = (HttpURLConnection) src.openConnection();
			
			MediaType mediaType = getMediaType(urlConnection.getContentType());
			if (MediaType.TEXT_HTML_TYPE.equals(mediaType)) {
				BrowserFrame browserFrame = new BrowserFrame(null, new ExternalResource(src));
				browserFrame.setSizeFull();
				content = browserFrame;
			} else if (MediaType.TEXT_PLAIN_TYPE.isCompatible(mediaType)) {
				content = new Label((String) urlConnection.getContent());
			} else if (IMAGE_TYPE.isCompatible(mediaType)) {
				Image image = new Image(null, new ExternalResource(src));
				image.setSizeUndefined();
				content = image;
			} else if (PDF_TYPE.isCompatible(mediaType)) {
				Embedded embedded = new Embedded(null, new ExternalResource(src));
				embedded.setSizeFull();
				content = embedded;
			} else {
				Button btnDownload = new Button("Download resource");
				btnDownload.addStyleName(ValoTheme.BUTTON_FRIENDLY);
				new FileDownloader(new ExternalResource(src)).extend(btnDownload);
				content = btnDownload;
			}

		} catch (IOException e) {
			e.printStackTrace();
			Label label = new Label(String.format("There was an error with the resource!\n%s", e.getMessage()));
			label.addStyleName(ValoTheme.LABEL_FAILURE);
			content = label;
		} finally {
			if (urlConnection != null) {
				urlConnection.disconnect();
			}
		}
		
		panel.setContent(content);
		
	}
	
	private MediaType getMediaType(String contentType) {
		MediaType mediaType = null;
		try {
			mediaType = MediaType.valueOf(contentType);
		} catch (IllegalArgumentException e) {
			mediaType = MediaType.APPLICATION_OCTET_STREAM_TYPE;
		}
		return mediaType;
	}

}
