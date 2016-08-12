package ch.hsr.isf.serepo.client.webapp.view;

import com.vaadin.event.ShortcutAction.KeyCode;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.shared.Position;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.client.webapp.event.AppEvent;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;

public class LoginView extends VerticalLayout {

	private static final long serialVersionUID = -7558969979367879398L;

	public LoginView() {

		setSizeFull();

		Component loginForm = buildLoginForm();
		addComponent(loginForm);
		setComponentAlignment(loginForm, Alignment.MIDDLE_CENTER);

		showNotification();

	}

	private void showNotification() {
		Notification notification = new Notification("Note");
		notification.setDescription("<span>This application demonstrates some but not all features of the RESTful HTTP interface of SE-Repo.</span> <span>The username as well as the email are used for identifying your operations on the SE-Repo.</span>");
		notification.setHtmlContentAllowed(true);
//		notification.setStyleName("tray dark small closable login-help");
		notification.setPosition(Position.BOTTOM_CENTER);
		notification.setDelayMsec(60000);
		notification.show(Page.getCurrent());
	}

	private Component buildLoginForm() {
		final VerticalLayout loginPanel = new VerticalLayout();
		loginPanel.setSizeUndefined();
		loginPanel.setSpacing(true);
		Responsive.makeResponsive(loginPanel);
		loginPanel.addStyleName("login-panel");

		loginPanel.addComponent(buildLabels());
		loginPanel.addComponent(buildFields());
		return loginPanel;
	}

	private Component buildFields() {
		VerticalLayout fields = new VerticalLayout();
		fields.setSpacing(true);
		fields.addStyleName("fields");

		final TextField username = new TextField("Username");
		username.setIcon(FontAwesome.USER);
		username.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

		final TextField email = new TextField("E-Mail");
		email.setIcon(FontAwesome.AT);
		email.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
		
//		final PasswordField password = new PasswordField("Password");
//		password.setIcon(FontAwesome.LOCK);
//		password.addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);

		final Button signin = new Button("Sign In");
		signin.addStyleName(ValoTheme.BUTTON_PRIMARY);
		signin.setClickShortcut(KeyCode.ENTER);
		signin.focus();

		fields.addComponents(username, email, signin);
		fields.setComponentAlignment(signin, Alignment.BOTTOM_LEFT);

		signin.addClickListener(new ClickListener() {
			private static final long serialVersionUID = 5232025898075820299L;
			@Override
			public void buttonClick(final ClickEvent event) {
				AppEventBus.post(new AppEvent.UserLoginRequestEvent(username.getValue(), email.getValue()));
			}
		});
		return fields;
	}

	private Component buildLabels() {
		CssLayout labels = new CssLayout();
		labels.addStyleName("labels");

		Label welcome = new Label("Welcome");
		welcome.setSizeUndefined();
		welcome.addStyleName(ValoTheme.LABEL_H4);
		welcome.addStyleName(ValoTheme.LABEL_COLORED);
		labels.addComponent(welcome);

		Label title = new Label("SE-Repo Client Webapplication");
		title.setSizeUndefined();
		title.addStyleName(ValoTheme.LABEL_H3);
		title.addStyleName(ValoTheme.LABEL_LIGHT);
		labels.addComponent(title);
		return labels;
	}

}
