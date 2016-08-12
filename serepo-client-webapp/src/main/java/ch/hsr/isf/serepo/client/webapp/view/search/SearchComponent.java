package ch.hsr.isf.serepo.client.webapp.view.search;

import java.util.List;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.BrowserWindowOpener;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.shared.ui.label.ContentMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.TextField;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import elemental.events.KeyboardEvent.KeyCode;

public class SearchComponent extends CustomComponent {

  private static final long serialVersionUID = 3479358377215890893L;

  public interface Listener {
    
    void searchClicked(String repository, String commitId, String searchIn, String query);

    void repositoryChanged(String repository);
    
  }

  public static class CommitInfo {
    private String id;
    private String shortMessage;

    public CommitInfo(String id, String shortMessage) {
      this.id = id;
      this.shortMessage = shortMessage;
    }

    public String getId() {
      return id;
    }

    public String getShortMessage() {
      return shortMessage;
    }
  }

  private VerticalLayout vl;
  private TextField txtfSearchQuery;
  private ComboBox cmbxRepository;
  private ComboBox cmbxCommit;
  private ComboBox cmbxSearchIn;

  private Listener listener = null;

  public SearchComponent() {
		
    setSizeUndefined();
    vl = new VerticalLayout();
    vl.setWidth("100%");
    vl.setSpacing(true);
    setCompositionRoot(vl);

    txtfSearchQuery = new TextField("Search Query");
    txtfSearchQuery.setWidth("100%");
    txtfSearchQuery.setIcon(FontAwesome.SEARCH);
    txtfSearchQuery.setInputPrompt("term or complex query with AND, OR, NOT");
    
    Button btnSearch = new Button("Search", new ClickListener() {
      private static final long serialVersionUID = 6224557624748707379L;

      @Override
      public void buttonClick(ClickEvent event) {
        if (listener != null) {
          listener.searchClicked((String) cmbxRepository.getValue(), (String) cmbxCommit.getValue(),
              (String) cmbxSearchIn.getValue(), txtfSearchQuery.getValue());
        }
      }
    });
    btnSearch.setIcon(FontAwesome.SEARCH);
    btnSearch.setClickShortcut(KeyCode.ENTER);
    
    cmbxRepository = new ComboBox("Repository");
    cmbxRepository.setWidth("100%");
    cmbxRepository.setIcon(FontAwesome.DATABASE);
    cmbxRepository.setInputPrompt("Restrict to repository");
    cmbxRepository.setFilteringMode(FilteringMode.CONTAINS);
    cmbxRepository.addValueChangeListener(new ValueChangeListener() {
      private static final long serialVersionUID = -2796627300174190136L;

      @Override
      public void valueChange(ValueChangeEvent event) {
        listener.repositoryChanged((String) event.getProperty()
                                                 .getValue());
      }
    });

    cmbxCommit = new ComboBox("Commit");
    cmbxCommit.setWidth("100%");
    cmbxCommit.setIcon(FontAwesome.PENCIL_SQUARE_O);
    cmbxCommit.setInputPrompt("Restrict to commit");
    cmbxCommit.setFilteringMode(FilteringMode.CONTAINS);

    cmbxSearchIn = new ComboBox("Search...");
    cmbxSearchIn.setWidth("100%");
    cmbxSearchIn.setIcon(FontAwesome.LOCATION_ARROW);
    cmbxSearchIn.setInputPrompt("everywhere");
    cmbxSearchIn.addItems("metadata", "content");
    cmbxSearchIn.setItemCaption("metadata", "only in metadata");
    cmbxSearchIn.setItemCaption("content", "only in content");

    HorizontalLayout hlSearch = new HorizontalLayout(txtfSearchQuery, btnSearch);
    hlSearch.setWidth("100%");
    hlSearch.setExpandRatio(txtfSearchQuery, 1.0f);
    hlSearch.setComponentAlignment(btnSearch, Alignment.BOTTOM_CENTER);
    
    vl.addComponent(hlSearch);
    vl.addComponent(createExampleLabel());
    vl.addComponent(createHelpLink());
    vl.addComponent(cmbxRepository);
    vl.addComponent(cmbxCommit);
    vl.addComponent(cmbxSearchIn);

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

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  public void setRepositories(List<String> repositories) {
    cmbxRepository.removeAllItems();
    cmbxRepository.setValue(null);
    cmbxRepository.addItems(repositories);
  }

  public void setCommits(List<CommitInfo> commits) {
    cmbxCommit.removeAllItems();
    cmbxCommit.setValue(null);
    for (CommitInfo commit : commits) {
      cmbxCommit.addItem(commit.getId());
      String shortCommitId = commit.getId()
                                   .substring(0, 8);
      String caption = String.format("%s - %s", shortCommitId, commit.getShortMessage());
      cmbxCommit.setItemCaption(commit.getId(), caption);
    }
  }

}
