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
import ch.hsr.isf.serepo.client.webapp.view.search.SearchField;
import ch.hsr.isf.serepo.client.webapp.view.search.SearchField.SearchRequest;

public class MainView extends VerticalLayout {

  private static final long serialVersionUID = -3799551812127454236L;
  private Label lblTitle = new Label();
  private SearchField searchField;
  private VerticalLayout vlSearchField;

  public MainView() {

    setSizeFull();
    setMargin(true);
    setSpacing(true);

    lblTitle.addStyleName(ValoTheme.LABEL_H2);
    lblTitle.addStyleName(ValoTheme.LABEL_COLORED);
    
    searchField = new SearchField();
    searchField.setClearAfterSearch(true);
    searchField.addStyleName(ValoTheme.TEXTFIELD_SMALL);
    // due to a bug in vaadin we need to wrap the searchField into a vertical-layout.
    // Otherwise the inline icon will be mispaced.
    vlSearchField = new VerticalLayout(searchField);
    vlSearchField.setSizeUndefined();
    
    MenuBar menuBar = createMenuBar();

    HorizontalLayout hlHeader = new HorizontalLayout(lblTitle, vlSearchField, menuBar);
    addComponent(hlHeader);
    hlHeader.setExpandRatio(lblTitle, 1);
    hlHeader.setComponentAlignment(lblTitle, Alignment.MIDDLE_LEFT);
    hlHeader.setComponentAlignment(vlSearchField, Alignment.MIDDLE_RIGHT);
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
  private void onSearchRequest(SearchRequest request) {
    AppNavigator.navigateTo(AppViewType.SEARCH, request.getQuery());
  }

  @Subscribe
  public void changeTitle(AppEvent.TitleChangeEvent event) {
    lblTitle.setValue(event.getTitle());
  }
  
  @Subscribe
  public void setSearchFieldVisible(AppEvent.GlobalSearchField.Visible field) {
    vlSearchField.removeComponent(searchField);
    if (field.isVisible()) {
     vlSearchField.addComponent(searchField); 
    }
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
  
  @Override
  public void attach() {
    super.attach();
    AppEventBus.register(this);
  }

  @Override
  public void detach() {
    AppEventBus.unregister(this);
    super.detach();
  }

}
