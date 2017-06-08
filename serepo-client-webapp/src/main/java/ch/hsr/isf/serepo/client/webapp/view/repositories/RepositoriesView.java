package ch.hsr.isf.serepo.client.webapp.view.repositories;

import java.util.ArrayList;
import java.util.List;

import com.google.common.base.Optional;
import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
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
import ch.hsr.isf.serepo.client.webapp.window.PromptWindow;
import ch.hsr.isf.serepo.client.webapp.window.PromptWindow.Answer;
import ch.hsr.isf.serepo.client.webapp.window.PromptWindow.AnswerCall;
import ch.hsr.isf.serepo.client.webapp.window.PromptWindow.Mode;
import ch.hsr.isf.serepo.data.restinterface.repository.Repository;

public class RepositoriesView extends MasterActionLayout implements View, IRepositoriesView {

  private static final long serialVersionUID = -8535035463609581411L;

  private RepositoryPresenter presenter;
  private final RepositoriesContainer repositoriesContainer = new RepositoriesContainer();

  public RepositoriesView() {

    AppEventBus.post(new AppEvent.TitleChangeEvent("Repositories in SE-Repo"));

    setMasterComponent(repositoriesContainer);
    setActionComponents(createActions());

  }

  private List<Button> createActions() {
    List<Button> btns = new ArrayList<>();
    btns.add(createButton("Show commits", AppViewType.COMMITS.getIcon(), ValoTheme.BUTTON_PRIMARY,
        new ClickListener() {
          private static final long serialVersionUID = 8964842802612343459L;

          @Override
          public void buttonClick(ClickEvent event) {
            processSelectedRepository(new RepositoryCallback() {
              @Override
              public void callback(Repository repository) {
                navigateToCommits(repository);
              }
            });
          }
        }));
    btns.add(createButton("Create new repository", FontAwesome.PLUS, ValoTheme.BUTTON_FRIENDLY,
        new ClickListener() {
          private static final long serialVersionUID = 8964842802612343459L;

          @Override
          public void buttonClick(ClickEvent event) {
            CreateRepositoryWindow window = new CreateRepositoryWindow();
            window.setListener(new CreateRepositoryWindow.Listener() {
              @Override
              public void created(String repositoryName) {
                presenter.load();
              }
            });
            window.show();
          }
        }));
    btns.add(createButton("Delete repository", FontAwesome.REMOVE, ValoTheme.BUTTON_DANGER,
        new ClickListener() {
          private static final long serialVersionUID = 8964842802612343459L;

          @Override
          public void buttonClick(ClickEvent event) {
            processSelectedRepository(new RepositoryCallback() {

              @Override
              public void callback(final Repository repository) {
                new PromptWindow(null,
                    String.format("Do you really want to delete the repository '%s'?",
                        repository.getName()),
                    Mode.YES_NO, new AnswerCall() {
                      @Override
                      public void answer(Answer answer) {
                        if (Answer.YES == answer) {
                          presenter.deleteRepository(repository.getName());
                        }
                      }
                    });
              }
            });
          }
        }));
    return btns;
  }

  private Button createButton(String caption, Resource icon, String css,
      ClickListener clickListener) {
    Button btn = new Button(caption, icon);
    btn.addClickListener(clickListener);
    if (!Strings.isNullOrEmpty(css)) {
      btn.addStyleName(css);
    }
    return btn;
  }

  private interface RepositoryCallback {
    void callback(Repository repository);
  }

  private void processSelectedRepository(RepositoryCallback callback) {
    Optional<Repository> selectedRepository = repositoriesContainer.getSelectedRepository();
    if (selectedRepository.isPresent()) {
      callback.callback(selectedRepository.get());
    } else {
      Notification.show("Please select a repository.", Type.WARNING_MESSAGE);
    }
  }
  
  private void navigateToCommits(Repository repository) {
    AppNavigator.navigateTo(AppViewType.COMMITS, repository.getName());
  }

  @Override
  public void setRepositories(List<Repository> repositories) {
    repositoriesContainer.setRepositories(repositories);
  }

  @Override
  public void attach() {
    super.attach();
    presenter = new RepositoryPresenter(this);
    AppEventBus.register(this);
  }

  @Override
  public void detach() {
    AppEventBus.unregister(this);
    super.detach();
  }

  @Subscribe
  public void itemDoubleClicked(AppEvent.ItemDoubleClickevent<Repository> event) {
    navigateToCommits(event.getItem());
  }
  
  @Override
  public void enter(ViewChangeEvent event) {}

}
