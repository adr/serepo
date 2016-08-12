package ch.hsr.isf.serepo.client.webapp.view;

import com.google.common.eventbus.Subscribe;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.CssLayout;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.MenuBar;
import com.vaadin.ui.MenuBar.Command;
import com.vaadin.ui.MenuBar.MenuItem;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.client.webapp.AppNavigator;
import ch.hsr.isf.serepo.client.webapp.event.AppEvent;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;
import ch.hsr.isf.serepo.client.webapp.model.User;

public class MainView extends VerticalLayout {

  private static final long serialVersionUID = -3799551812127454236L;
  private Label lblTitle = new Label();

  public MainView() {

    setSizeFull();
    setMargin(true);
    setSpacing(true);

    AppEventBus.register(this);

    lblTitle.addStyleName(ValoTheme.LABEL_H2);
    lblTitle.addStyleName(ValoTheme.LABEL_COLORED);
    MenuBar menuBar = createMenuBar();

    HorizontalLayout hlHeader = new HorizontalLayout(lblTitle, menuBar);
    addComponent(hlHeader);
    hlHeader.setExpandRatio(lblTitle, 1);
    hlHeader.setComponentAlignment(lblTitle, Alignment.MIDDLE_LEFT);
    hlHeader.setComponentAlignment(menuBar, Alignment.MIDDLE_RIGHT);
    hlHeader.setSpacing(true);
    hlHeader.setWidth("100%");
    hlHeader.setHeightUndefined();

    ComponentContainer content = new CssLayout();
    content.setSizeFull();

    addComponent(content);
    setExpandRatio(content, 1);

    new AppNavigator(content);
  }

  @Subscribe
  public void changeTitle(AppEvent.TitleChangeEvent event) {
    lblTitle.setValue(event.getTitle());
  }

  private MenuBar createMenuBar() {
    MenuBar menuBar = new MenuBar();
    menuBar.addStyleName(ValoTheme.MENUBAR_BORDERLESS);
    menuBar.setSizeUndefined();
    menuBar.addItem("Repositories", AppViewType.REPOSITORIES.getIcon(), new Command() {
      private static final long serialVersionUID = -7242763961104214263L;

      @Override
      public void menuSelected(MenuItem selectedItem) {
        AppNavigator.navigateTo(AppViewType.REPOSITORIES);
      }
    });
    menuBar.addItem("Search", AppViewType.SEARCH.getIcon(), new Command() {
      private static final long serialVersionUID = -399469459480685859L;

      @Override
      public void menuSelected(MenuItem selectedItem) {
        AppNavigator.navigateTo(AppViewType.SEARCH);
      }
    });
    User user = (User) VaadinSession.getCurrent()
                                    .getAttribute(User.class.getName());
    MenuItem userMenuItem = menuBar.addItem(user.getUsername(), FontAwesome.USER, null);
    userMenuItem.addItem("Logout", FontAwesome.SIGN_OUT, new Command() {
      private static final long serialVersionUID = -8662614978475295370L;

      @Override
      public void menuSelected(MenuItem selectedItem) {
        AppEventBus.post(new AppEvent.UserLogoutEvent());
      }
    });
    return menuBar;
  }

}
