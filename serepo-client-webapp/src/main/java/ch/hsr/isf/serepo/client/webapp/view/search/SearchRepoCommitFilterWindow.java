package ch.hsr.isf.serepo.client.webapp.view.search;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.server.FontAwesome;
import com.vaadin.shared.ui.combobox.FilteringMode;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.client.webapp.services.SeRepoRestAPI;
import ch.hsr.isf.serepo.data.restinterface.commit.Commit;
import ch.hsr.isf.serepo.data.restinterface.commit.CommitContainer;
import ch.hsr.isf.serepo.data.restinterface.repository.Repository;
import ch.hsr.isf.serepo.data.restinterface.repository.RepositoryContainer;

public class SearchRepoCommitFilterWindow extends Window {
  
  private static final long serialVersionUID = 4912426611058241808L;

  private ComboBox cmbxRepositories = new ComboBox("Repository");
  private ComboBox cmbxCommits = new ComboBox("Commit");
  private BeanItemContainer<Commit> commitsContainer = new BeanItemContainer<>(Commit.class);
  
  private VerticalLayout vlMain = new VerticalLayout();
  private Button btnAddToQuery = new Button("Add to query", FontAwesome.PLUS);
  
  public interface RepoCommitFilterListener {
    void filter(String query);
  }
  private RepoCommitFilterListener listener;
  
  public SearchRepoCommitFilterWindow(RepoCommitFilterListener listener) {
    
    this.listener = listener;
    setCaption("Add filters...");
    setIcon(FontAwesome.FILTER);
    setWidth("30%");
    setResizable(false);
    setHeightUndefined();
    center();
    
    configComponents();
    configMainLayout();
    
    setContent(vlMain);
    open();
    
  }

  private void configMainLayout() {
    vlMain.setSpacing(true);
    vlMain.setMargin(true);
    vlMain.addComponent(cmbxRepositories);
    vlMain.addComponent(cmbxCommits);
    vlMain.addComponent(btnAddToQuery);
    vlMain.setComponentAlignment(btnAddToQuery, Alignment.BOTTOM_RIGHT);
  }

  private void configComponents() {
    configRepositoryComboBox();
    configCommitsComboBox();
    configButton();
  }

  private void configRepositoryComboBox() {
    cmbxRepositories.setIcon(FontAwesome.DATABASE);
    cmbxRepositories.setWidth("100%");
    cmbxRepositories.setNullSelectionAllowed(false);
    cmbxRepositories.setFilteringMode(FilteringMode.CONTAINS);
    cmbxRepositories.setInputPrompt("Restrict to repository...");
    RepositoryContainer repositoryContainer = SeRepoRestAPI.getRepositories();
    for (Repository repo : repositoryContainer.getRepositories()) {
      cmbxRepositories.addItem(repo.getName());
    }
    cmbxRepositories.addValueChangeListener(new ValueChangeListener() {
      private static final long serialVersionUID = -8321111149153255573L;

      @Override
      public void valueChange(ValueChangeEvent event) {
        CommitContainer commitContainer = SeRepoRestAPI.getCommitsForRepository((String) event.getProperty().getValue());
        cmbxCommits.removeAllItems();
        for (Commit commit : commitContainer.getCommits()) {
          cmbxCommits.addItem(commit);
          cmbxCommits.setItemCaption(commit, commit.getShortMessage() + " [" + commit.getCommitId() + "]");
        }
        cmbxCommits.setVisible(true);
        center();
      }
    });
  }

  private void configCommitsComboBox() {
    cmbxCommits.setContainerDataSource(commitsContainer);
    cmbxCommits.setIcon(FontAwesome.HISTORY);
    cmbxCommits.setWidth("100%");
    cmbxCommits.setFilteringMode(FilteringMode.CONTAINS);
    cmbxCommits.setNullSelectionAllowed(true);
    cmbxCommits.setInputPrompt("(Optional) restrict to commit...");
    cmbxCommits.setVisible(false);
  }

  private void configButton() {
    btnAddToQuery.addStyleName(ValoTheme.BUTTON_PRIMARY);
    btnAddToQuery.addClickListener(new ClickListener() {
      private static final long serialVersionUID = -8294377110903575679L;

      @Override
      public void buttonClick(ClickEvent event) {
        addToQueryClicked();
      }
    });
  }

  private void addToQueryClicked() {
    StringBuilder query = new StringBuilder();
    if (cmbxRepositories.getValue() != null) {
      query.append("repository:").append(cmbxRepositories.getValue().toString()).append(" AND ");
      if (cmbxCommits.getValue() != null) {
        query.append("commitid:").append(((Commit) cmbxCommits.getValue()).getCommitId()).append(" AND ");
      }
      listener.filter(query.toString());
    }
    this.close();
  }

  private void open() {
    UI.getCurrent()
      .addWindow(this);
  }

}
