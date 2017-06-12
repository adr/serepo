package ch.hsr.isf.serepo.client.webapp.view.seitems;

import java.util.LinkedList;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.shared.ui.MarginInfo;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.ComboBox;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.client.webapp.AppNavigator;
import ch.hsr.isf.serepo.client.webapp.model.Settings;
import ch.hsr.isf.serepo.client.webapp.view.AppViewType;
import ch.hsr.isf.serepo.data.restinterface.commit.Commit;
import ch.hsr.isf.serepo.data.restinterface.commit.CommitContainer;

public class CommitInfoComponent extends CustomComponent {

  private HorizontalLayout layout = new HorizontalLayout() {

    @Override
    public void addComponent(Component c) {
      super.addComponent(c);
      super.setComponentAlignment(c, Alignment.MIDDLE_LEFT);
    }
    
  };
  
  private Label lblRepository = new Label();
  private ComboBox cmbxCommits = new ComboBox();
  private boolean cmbxCommitVclEnabled = false;
  
  public CommitInfoComponent() {
    createLayout();
    setCompositionRoot(layout);
  }
  
  private void createLayout() {
    cmbxCommits.addStyleName(ValoTheme.COMBOBOX_SMALL);
    cmbxCommits.addStyleName(ValoTheme.COMBOBOX_BORDERLESS);
    cmbxCommits.setScrollToSelectedItem(true);
    cmbxCommits.setNullSelectionAllowed(false);
    cmbxCommits.setDescription("Change commit");
    cmbxCommits.addValueChangeListener(new ValueChangeListener() {
      
      @Override
      public void valueChange(ValueChangeEvent event) {
        if (cmbxCommitVclEnabled) {
          AppNavigator.navigateTo(AppViewType.SEITEMS, lblRepository.getValue(), event.getProperty().getValue().toString());
        }
      }
    });

    layout.setSpacing(true);
    layout.setMargin(new MarginInfo(false, false, true, false));
    layout.addComponent(createActionButton("Repository:", FontAwesome.DATABASE, false, new ClickListener() {
      
      @Override
      public void buttonClick(ClickEvent event) {
        AppNavigator.navigateTo(AppViewType.REPOSITORIES);
      }
    }));
    layout.addComponent(lblRepository);
    layout.addComponent(createActionButton("Commit-ID:", FontAwesome.CODE_FORK, true, new ClickListener() {
      
      @Override
      public void buttonClick(ClickEvent event) {
        AppNavigator.navigateTo(AppViewType.COMMITS, lblRepository.getValue());
      }
    }));
    layout.addComponent(cmbxCommits);
  }
  
  private Button createActionButton(String caption, Resource icon, boolean addSpace, ClickListener action) {
    Button btn = new Button(caption, icon);
    btn.addClickListener(action);
    btn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
    btn.addStyleName(ValoTheme.BUTTON_LINK);
    btn.addStyleName(ValoTheme.BUTTON_SMALL);
    if (addSpace) {
      btn.addStyleName("left-space-30");
    }
    return btn;
  }
  
  public void setRepository(String repository) {
    lblRepository.setValue(repository);
    LinkedList<Commit> commits = loadCommits(repository);
    addCommitsToComboBox(commits);
  }

  private void addCommitsToComboBox(LinkedList<Commit> commits) {
    cmbxCommitVclEnabled = false;
    try {
      cmbxCommits.removeAllItems();
      for (Commit commit : commits) {
        cmbxCommits.addItem(commit.getCommitId());
        String commitIdShort = commit.getCommitId().substring(0, 6);
        cmbxCommits.setItemCaption(commit.getCommitId(), String.format("%s - %s", commitIdShort, commit.getShortMessage()));
      }
    } finally {
      cmbxCommitVclEnabled = true;
    }
  }
  
  private LinkedList<Commit> loadCommits(String repository) {
    String uri = String.format("%s/repos/%s/commits", Settings.getFromSession().getSerepoUrl(), repository);
    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(uri);
    Response response = null;
    try {
      response = target.request()
                       .accept(MediaType.APPLICATION_JSON_TYPE)
                       .get();
      if (response.getStatus() == Status.OK.getStatusCode()) {
        CommitContainer commitContainer = response.readEntity(CommitContainer.class);
        return new LinkedList<>(commitContainer.getCommits());
      } else {
        return new LinkedList<>();
      }
    } finally {
      if (response != null) {
        response.close();
      }
    }
  }

  public void setCommitId(String commitId) {
    if (cmbxCommits.isEmpty()) {
      cmbxCommits.addItem(commitId);
    }
    cmbxCommitVclEnabled = false;
    try {
      cmbxCommits.select(commitId);
    } finally {
      cmbxCommitVclEnabled = true;
    }
  }
  
}
