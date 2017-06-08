package ch.hsr.isf.serepo.client.webapp;

import java.util.HashMap;
import java.util.Locale;

import javax.servlet.annotation.WebServlet;

import com.google.common.eventbus.Subscribe;
import com.vaadin.annotations.Theme;
import com.vaadin.annotations.Title;
import com.vaadin.annotations.VaadinServletConfiguration;
import com.vaadin.server.ErrorHandler;
import com.vaadin.server.Page;
import com.vaadin.server.Responsive;
import com.vaadin.server.VaadinRequest;
import com.vaadin.server.VaadinServlet;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.UI;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.client.webapp.event.AppEvent;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;
import ch.hsr.isf.serepo.client.webapp.model.Settings;
import ch.hsr.isf.serepo.client.webapp.model.User;
import ch.hsr.isf.serepo.client.webapp.view.LoginView;
import ch.hsr.isf.serepo.client.webapp.view.MainView;

@Theme("mytheme")
@Title("SE-Repo Webclient")
public class MyUI extends UI {

  private final AppEventBus appEventBus = new AppEventBus();

  @Override
  protected void init(VaadinRequest vaadinRequest) {
    setLocale(Locale.US);
    setErrorHandler(new ErrorHandler() {
      private static final long serialVersionUID = 3633312183020982434L;

      @Override
      public void error(com.vaadin.server.ErrorEvent event) {
        event.getThrowable()
             .printStackTrace();
        Notification.show(event.getThrowable()
                               .getMessage(),
            Type.ERROR_MESSAGE);
      }
    });
    AppEventBus.register(this);
    Responsive.makeResponsive(this);
    addStyleName(ValoTheme.UI_WITH_MENU);
//    VaadinSession.getCurrent().setAttribute(User.class.getName(), new User("WebUser", "user@serepo.com")); // TODO automatic login
    VaadinSession.getCurrent()
                 .setAttribute(Settings.class.getName(), Settings.read());
    if (VaadinSession.getCurrent().getAttribute("table-selections") == null) {
      VaadinSession.getCurrent().setAttribute("table-selections", new HashMap<Class<?>, Object>());
    }
    updateContent();
  }

  private void updateContent() {
    User user = (User) VaadinSession.getCurrent()
                                    .getAttribute(User.class.getName());
    if (user != null) {
      setContent(new MainView());
      removeStyleName("loginview");
      getNavigator().navigateTo(getNavigator().getState());
    } else {
      setContent(new LoginView());
      addStyleName("loginview");
    }
  }

  @Subscribe
  public void userLoginRequested(AppEvent.UserLoginRequestEvent event) {
    User user = Authentication.authenticate(event.getUsername(), event.getEmail());
    VaadinSession.getCurrent()
                 .setAttribute(User.class.getName(), user);
    updateContent();
  }

  @Subscribe
  public void userLoggedOut(AppEvent.UserLogoutEvent event) {
    // Notice the this doesn't invalidate the current HttpSession.
    VaadinSession.getCurrent()
                 .close();
    Page.getCurrent().setLocation("");
  }

  public static AppEventBus getAppEventbus() {
    return ((MyUI) getCurrent()).appEventBus;
  }

  @WebServlet(urlPatterns = "/*", name = "MyUIServlet", asyncSupported = true)
  @VaadinServletConfiguration(ui = MyUI.class, productionMode = false)
  public static class MyUIServlet extends VaadinServlet {
  }

}
