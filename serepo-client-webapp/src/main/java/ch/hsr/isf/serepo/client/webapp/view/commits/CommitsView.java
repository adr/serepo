package ch.hsr.isf.serepo.client.webapp.view.commits;

import java.util.Arrays;
import java.util.List;

import com.google.common.base.Optional;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.client.webapp.AppNavigator;
import ch.hsr.isf.serepo.client.webapp.event.AppEvent;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;
import ch.hsr.isf.serepo.client.webapp.view.AppViewType;
import ch.hsr.isf.serepo.client.webapp.view.MasterActionLayout;
import ch.hsr.isf.serepo.data.restinterface.commit.Commit;

public class CommitsView extends MasterActionLayout implements View, ICommitsView {

  private static final long serialVersionUID = -3898473122989097531L;

  private CommitsPresenter presenter;
  private CommitContainer commitContainer = new CommitContainer();

  public CommitsView() {
    setMasterComponent(commitContainer);
    setActionComponents(createActions());
  }

  private List<Button> createActions() {
    Button btnShowSeItems = new Button("Show SE-Items", new ClickListener() {
      private static final long serialVersionUID = -5037931762205645801L;

      @Override
      public void buttonClick(ClickEvent event) {
        processSelectedRepository(new CommitCallback() {
          @Override
          public void callback(Commit commit) {
            AppNavigator.navigateTo(AppViewType.SEITEMS, getRepository(commit),
                getCommitId(commit));
          }
        });
      }
    });
    btnShowSeItems.addStyleName(ValoTheme.BUTTON_PRIMARY);
    btnShowSeItems.setIcon(AppViewType.SEITEMS.getIcon());

    Button btnCheckConsistencyRelations = new Button("Check relations", new ClickListener() {
      private static final long serialVersionUID = -4483566291673913833L;

      @Override
      public void buttonClick(ClickEvent event) {
        processSelectedRepository(new CommitCallback() {
          @Override
          public void callback(Commit commit) {
            AppNavigator.navigateTo(AppViewType.CONSISTENCY, getRepository(commit), getCommitId(commit));
          }
        });
      }
    });
    // TODO button for searching
    return Arrays.asList(btnShowSeItems, btnCheckConsistencyRelations);
  }

  private String getRepository(Commit commit) {
    String[] uri = commit.getId()
                         .toString()
                         .split("/");
    return uri[uri.length - 3];
  }

  private String getCommitId(Commit commit) {
    String[] uri = commit.getId()
                         .toString()
                         .split("/");
    return uri[uri.length - 1];
  }

  private interface CommitCallback {
    void callback(Commit commit);
  }

  private void processSelectedRepository(CommitCallback callback) {
    Optional<Commit> selectedCommit = commitContainer.getSelectedCommit();
    if (selectedCommit.isPresent()) {
      callback.callback(selectedCommit.get());
    } else {
      Notification.show("Please select a commit.", Type.WARNING_MESSAGE);
    }
  }

  @Override
  public void setCommits(List<Commit> commits) {
    commitContainer.setCommits(commits);
  }

  @Override
  public void attach() {
    super.attach();
    presenter = new CommitsPresenter(this);
  }

  @Override
  public void enter(ViewChangeEvent event) {
    if (event.getParameters() != null) {
      String repository = event.getParameters()
                               .split("/")[0];
      presenter.load(repository);
      String title = String.format("Commits in repository '%s'", repository);
      AppEventBus.post(new AppEvent.TitleChangeEvent(title));
    }
  }

}
