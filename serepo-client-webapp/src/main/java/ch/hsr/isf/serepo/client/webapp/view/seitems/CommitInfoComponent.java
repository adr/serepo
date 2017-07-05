package ch.hsr.isf.serepo.client.webapp.view.seitems;

import java.util.LinkedList;

import com.vaadin.data.Property.ValueChangeEvent;
import com.vaadin.data.Property.ValueChangeListener;
import com.vaadin.jsclipboard.ClipboardButton;
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
import com.vaadin.ui.Notification;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.client.webapp.AppNavigator;
import ch.hsr.isf.serepo.client.webapp.services.SeRepoRestAPI;
import ch.hsr.isf.serepo.client.webapp.view.AppViewType;
import ch.hsr.isf.serepo.data.restinterface.commit.Commit;
import ch.hsr.isf.serepo.data.restinterface.commit.CommitContainer;
import ch.hsr.isf.serepo.data.restinterface.repository.Repository;
import ch.hsr.isf.serepo.data.restinterface.repository.RepositoryContainer;

public class CommitInfoComponent extends CustomComponent {
  private static final long serialVersionUID = -9203631624167358267L;

  private HorizontalLayout layout = new HorizontalLayout() {
    private static final long serialVersionUID = 2725271231172732137L;

    @Override
    public void addComponent(Component c) {
      super.addComponent(c);
      super.setComponentAlignment(c, Alignment.MIDDLE_LEFT);
    }
    
  };
  
  private ComboBox cmbxRepositories = new ComboBox();
  private ComboBox cmbxCommits = new ComboBox();
  private boolean cmbxValueChangeListenerEnabled = false;
  private TextField tfCommitId = new TextField(null, "");
  
  public CommitInfoComponent() {
    createLayout();
    loadRepositories();
    setCompositionRoot(layout);
  }
  
  private void loadRepositories() {
    cmbxValueChangeListenerEnabled = false;
    try {
      cmbxRepositories.removeAllItems();
      RepositoryContainer repositoryContainer = SeRepoRestAPI.getRepositories();
      for (Repository repo : repositoryContainer.getRepositories()) {
        cmbxRepositories.addItem(repo.getName());
      }
    } finally {
      cmbxValueChangeListenerEnabled = true;
    }
  }

  private void createLayout() {
    layout.setSpacing(true);
    layout.setMargin(new MarginInfo(false, false, true, false));
    configRepositoryGroup();
    configCommitGroup();
  }

  private void configRepositoryGroup() {
    Button btnRepository = createActionButton("Repository:", FontAwesome.DATABASE, new ClickListener() {
      private static final long serialVersionUID = 47623509560655628L;

      @Override
      public void buttonClick(ClickEvent event) {
        AppNavigator.navigateTo(AppViewType.REPOSITORIES);
      }
    });
    btnRepository.setDescription("go to repositories");
    layout.addComponent(btnRepository);
    configRepositoriesCmbx();
    layout.addComponent(cmbxRepositories);
  }

  private void configCommitGroup() {
    Button btnCommit = createActionButton("Commit:", FontAwesome.HISTORY, new ClickListener() {
      private static final long serialVersionUID = -5239748636964294986L;

      @Override
      public void buttonClick(ClickEvent event) {
        AppNavigator.navigateTo(AppViewType.COMMITS, cmbxRepositories.getValue().toString());
      }
    });
    btnCommit.addStyleName("left-space-30");
    btnCommit.setDescription("go to commits");
    layout.addComponent(btnCommit);
    configCommitsCmbx();
    layout.addComponent(cmbxCommits);

    tfCommitId.setId("textfield-commitid");
    tfCommitId.setWidth("350px");
    tfCommitId.addStyleName(ValoTheme.TEXTFIELD_SMALL);
    layout.addComponent(tfCommitId);

    ClipboardButton clipboardButton = new ClipboardButton("textfield-commitid");
    clipboardButton.addSuccessListener(new ClipboardButton.SuccessListener() {
      private static final long serialVersionUID = -71258407509006880L;

        @Override
        public void onSuccess() {
            Notification.show("CommitId was copied to clipboard");
        }
    });
    clipboardButton.setClipboardButtonCaption("Copy");
    layout.addComponent(clipboardButton);
  }

  private void configRepositoriesCmbx() {
    cmbxRepositories.setWidth("300px");
    cmbxRepositories.setTextInputAllowed(false);
    cmbxRepositories.addStyleName(ValoTheme.COMBOBOX_SMALL);
    cmbxRepositories.setScrollToSelectedItem(true);
    cmbxRepositories.setNullSelectionAllowed(false);
    cmbxRepositories.setDescription("Change repository");
    cmbxRepositories.addValueChangeListener(new ValueChangeListener() {
      private static final long serialVersionUID = -7191898031310316511L;

      @Override
      public void valueChange(ValueChangeEvent event) {
        if (cmbxValueChangeListenerEnabled) {
          String repository = event.getProperty().getValue().toString();
          String commitId = SeRepoRestAPI.getCommitsForRepository(repository).getCommits().get(0).getCommitId();
          AppNavigator.navigateTo(AppViewType.SEITEMS, repository, commitId);
        }
      }
    });
  }

  private void configCommitsCmbx() {
    cmbxCommits.setWidth("700px");
    cmbxCommits.setTextInputAllowed(false);
    cmbxCommits.addStyleName(ValoTheme.COMBOBOX_SMALL);
    cmbxCommits.setScrollToSelectedItem(true);
    cmbxCommits.setNullSelectionAllowed(false);
    cmbxCommits.setDescription("Change commit");
    cmbxCommits.addValueChangeListener(new ValueChangeListener() {
      private static final long serialVersionUID = 8134832018266747215L;

      @Override
      public void valueChange(ValueChangeEvent event) {
        if (cmbxValueChangeListenerEnabled) {
          AppNavigator.navigateTo(AppViewType.SEITEMS, cmbxRepositories.getValue().toString(), event.getProperty().getValue().toString());
        }
      }
    });
  }
  
  private Button createActionButton(String caption, Resource icon, ClickListener action) {
    Button btn = new Button(caption, icon);
    btn.addClickListener(action);
    btn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
    btn.addStyleName(ValoTheme.BUTTON_LINK);
    btn.addStyleName(ValoTheme.BUTTON_SMALL);
    return btn;
  }
  
  public void setRepository(String repository) {
    cmbxValueChangeListenerEnabled = false;
    try {
      if (!cmbxRepositories.containsId(repository)) {
        cmbxRepositories.addItem(repository);
      }
      cmbxRepositories.select(repository);
      LinkedList<Commit> commits = loadCommits(repository);
      addCommitsToComboBox(commits);
    } finally {
      cmbxValueChangeListenerEnabled = true;
    }
  }

  private void addCommitsToComboBox(LinkedList<Commit> commits) {
    cmbxCommits.removeAllItems();
    for (Commit commit : commits) {
      cmbxCommits.addItem(commit.getCommitId());
      cmbxCommits.setItemCaption(commit.getCommitId(), String.format("%s [%s]", commit.getShortMessage(), commit.getCommitId()));
    }
  }
  
  private LinkedList<Commit> loadCommits(String repository) {
    CommitContainer commitsForRepository = SeRepoRestAPI.getCommitsForRepository(repository);
    return new LinkedList<>(commitsForRepository.getCommits());
  }

  public void setCommitId(String commitId) {
    if (!cmbxCommits.containsId(commitId)) {
      cmbxCommits.addItem(commitId);
    }
    cmbxValueChangeListenerEnabled = false;
    try {
      cmbxCommits.select(commitId);
    } finally {
      cmbxValueChangeListenerEnabled = true;
    }
    tfCommitId.setValue(commitId);
  }
  
}
