package ch.hsr.isf.serepo.client.webapp.view.search;

import com.google.common.eventbus.Subscribe;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.client.webapp.AppNavigator;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;
import ch.hsr.isf.serepo.client.webapp.services.SeRepoRestAPI;
import ch.hsr.isf.serepo.client.webapp.view.AppViewType;
import ch.hsr.isf.serepo.client.webapp.view.search.SearchField.SearchRequest;
import ch.hsr.isf.serepo.client.webapp.view.search.SearchRepoCommitFilterWindow.RepoCommitFilterListener;
import ch.hsr.isf.serepo.data.restinterface.search.SearchContainer;

public class SearchComponent extends CustomComponent {

  private static final long serialVersionUID = 3479358377215890893L;

  private VerticalLayout vl;
  private SearchField searchField = new SearchField();
  private Button btnSearch;
  private PopupView searchHelp;
  private Button btnRepoCommitFilter;
  
  public interface SearchResultListener {
    void searchResult(SearchContainer searchContainer);
  }
  private SearchResultListener listener;

  public SearchComponent() {
		
    setSizeUndefined();

    configSearchField();
    configSearchButton();
    configSearchHelp();
    configRepoCommitFilterButton();
    
    HorizontalLayout hlSearch = new HorizontalLayout(searchField, btnSearch);
    hlSearch.setWidth("100%");
    hlSearch.setExpandRatio(searchField, 1);
    hlSearch.setComponentAlignment(btnSearch, Alignment.BOTTOM_CENTER);

    HorizontalLayout hlFooter = new HorizontalLayout(searchHelp, btnRepoCommitFilter);
    hlFooter.setWidth("100%");
    hlFooter.setComponentAlignment(searchHelp, Alignment.MIDDLE_LEFT);
    hlFooter.setComponentAlignment(btnRepoCommitFilter, Alignment.MIDDLE_RIGHT);
    
    vl = new VerticalLayout(hlSearch, hlFooter);
    vl.setWidth("100%");
    setCompositionRoot(vl);
    
  }
  
  public void executeQuery(String query) {
    this.searchField.setValue(query);
    listener.searchResult(SeRepoRestAPI.search(query));
  }
  
  public void setListener(SearchResultListener listener) {
    this.listener = listener;
  }

  private void configSearchButton() {
    btnSearch = new Button("Search", new ClickListener() {
      private static final long serialVersionUID = 6224557624748707379L;

      @Override
      public void buttonClick(ClickEvent event) {
        if (!searchField.isEmpty()) {
          if (listener != null) {
            AppNavigator.navigateTo(AppViewType.SEARCH, searchField.getValue());
          }
        }
      }
    });
    btnSearch.setIcon(FontAwesome.SEARCH);
  }
  
  private void configSearchField() {
    searchField.setWidth("100%");
    searchField.setClearAfterSearch(false);
  }
  
  private void configSearchHelp() {
    Label helpText = createExampleLabel();
    VerticalLayout vl = new VerticalLayout(helpText, createHelpLink());
    vl.setSpacing(true);
    vl.setSizeUndefined();
    searchHelp = new PopupView(FontAwesome.INFO.getHtml() + " Search help", vl);
  }

  private Label createExampleLabel() {
    StringBuilder content = new StringBuilder();
    content.append("Examples:")
           .append("<br/>")
           .append("Search for a specific word in the given scope: <b>unrestricted</b>")
           .append("<br/>")
           .append("Search SE-Items which have a metatag with a given property: <b>intellectual_property_rights:Unrestricted</b>")
           .append("<br/>")
           .append("Search SE-Items which have specific metatag properties: <b>intellectual_property_rights:Unrestricted AND option_state:Chosen</b>")
           .append("<br/>")
           .append("Search SE-Items which have not a specific metatag: <b>NOT option_state:Chosen</b>")
           .append("<br/>")
           .append("Notes: Metatags have to be in lowercase; spaces must be replaced by underscore (_). E.g \"Option State\" -> \"option_state\"");
    return new Label(content.toString(), ContentMode.HTML);
  }
  
  private Button createHelpLink() {
    Button btn = new Button("Documentation of the underlying query processor");
    btn.addStyleName(ValoTheme.BUTTON_LINK);
    new BrowserWindowOpener(
        "https://cwiki.apache.org/confluence/display/solr/The+Standard+Query+Parser#TheStandardQueryParser-SpecifyingFieldsinaQuerytotheStandardQueryParser").extend(
            btn);
    return btn;
  }

  private void configRepoCommitFilterButton() {
    btnRepoCommitFilter = new Button("Filter...", FontAwesome.FILTER);
    btnRepoCommitFilter.addStyleName(ValoTheme.BUTTON_LINK);
    btnRepoCommitFilter.addClickListener(new ClickListener() {
      private static final long serialVersionUID = 4939366171347961386L;

      @Override
      public void buttonClick(ClickEvent event) {
        new SearchRepoCommitFilterWindow(new RepoCommitFilterListener() {
          
          @Override
          public void filter(String query) {
            searchField.setValue(query + searchField.getValue());
          }
        });
      }
    });
  }

  @Subscribe
  private void onSearchRequest(SearchRequest request) {
    if (listener != null) {
      listener.searchResult(SeRepoRestAPI.search(searchField.getValue()));
    }
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
