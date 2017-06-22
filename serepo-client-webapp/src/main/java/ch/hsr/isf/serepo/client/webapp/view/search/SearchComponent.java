package ch.hsr.isf.serepo.client.webapp.view.search;

import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.PopupView;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.client.webapp.AppNavigator;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;
import ch.hsr.isf.serepo.client.webapp.services.SeRepoRestAPI;
import ch.hsr.isf.serepo.client.webapp.services.SeRepoRestAPI.RestfulApiException;
import ch.hsr.isf.serepo.client.webapp.view.AppViewType;
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
    if (listener != null) {
      try {
        SearchContainer searchContainer = SeRepoRestAPI.search(query);
        listener.searchResult(searchContainer);
      } catch (RestfulApiException e) {
        Notification.show("Search failed!", e.getMessage(), Type.ERROR_MESSAGE);
      }
    }
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
          AppNavigator.navigateTo(AppViewType.SEARCH, searchField.getValue());
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
    Label helpText = createHelpTextLabel();
    searchHelp = new PopupView(FontAwesome.INFO.getHtml() + " Search help", helpText);
  }

  private Label createHelpTextLabel() {
    final String html =
        "<html>\n" + 
        "  <p><b>Search Query Help</b></p>\n" + 
        "  <ul>\n" + 
        "   <li>To search for a specific term just type the term. e.g.: <b>consistency</b></li>\n" + 
        "   <li>To search for a specific term with spaces just type the term. E.g.: <b>&quot;term consistency&quot;</b></li>\n" + 
        "   <li>A wildcard search can be done with <b>?</b> (matches a single character) or <b>*</b> (matches zero or more sequential characters).</li>\n" + 
        "   <li>To search for a SE-Item with a specific name, use the field <i>name</i>. E.g.: <b>name:consistency</b></li>\n" + 
        "   <li>To search/filter for metadata: <b>metadata_name_1:value1 AND metadata_name_2:value2</b><br/>\n" + 
        "       Note: The metadata name has to be in lower case and spaces replaced by underscore (_) E.g.: &quot;Option State&quot; -> &quot;option_state&quot;</li>\n" + 
        "   <li>To search/filter SE-Items which have not a specific metatag: <b>NOT option_state:Chosen</b>\n" + 
        "   <li>To search/filter within a specific repository, use the repository field. E.g.: <b>repository:repo-name-1</b></li>\n" + 
        "   <li>To search/filter within a specific commit within a repository: <b>repository:repo-name-1 AND commitid:76ba37*</b></li>\n" + 
        " </ul>\n" + 
        "  <a href=\"https://cwiki.apache.org/confluence/display/solr/The+Standard+Query+Parser#TheStandardQueryParser-SpecifyingFieldsinaQuerytotheStandardQueryParser\" target=\"_blank\">Documentation of the underlying query processor</a> \n" + 
        "</html>";
    return new Label(html, ContentMode.HTML);
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
