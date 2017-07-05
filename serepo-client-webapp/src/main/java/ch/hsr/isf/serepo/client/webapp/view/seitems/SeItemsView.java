package ch.hsr.isf.serepo.client.webapp.view.seitems;

import java.util.List;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.Panel;
import com.vaadin.ui.VerticalLayout;

import ch.hsr.isf.serepo.client.webapp.event.AppEvent;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;
import ch.hsr.isf.serepo.client.webapp.view.search.SearchField;
import ch.hsr.isf.serepo.client.webapp.view.seitems.containers.seitem.SeItemTreeContainer;
import ch.hsr.isf.serepo.client.webapp.view.seitems.containers.seitem.SeItemTreeItem;
import ch.hsr.isf.serepo.data.restinterface.seitem.SeItem;

public class SeItemsView extends VerticalLayout implements View, ISeItemsView {

  private static final long serialVersionUID = 1746669287835108355L;

  private SeItemsPresenter presenter;

  private final CommitInfoComponent commitInfo = new CommitInfoComponent();
  private final SeItemTreeContainer seItemsContainer = new SeItemTreeContainer();
  private Panel panelSeItem;
  private SeItemComponent seItemComponent = new SeItemComponent();

  public SeItemsView() {

    setSizeFull();

    addComponent(commitInfo);
    
    Panel panelSeItems = new Panel(seItemsContainer);
    panelSeItems.setSizeFull();
    panelSeItems.setIcon(seItemsContainer.getIcon());
    panelSeItems.setCaption(seItemsContainer.getCaption());
    
    seItemsContainer.setCaption("SE-Items");
    seItemsContainer.setListener(new SeItemTreeContainer.Listener() {

      @Override
      public void seItemClicked(SeItemTreeItem seItemTreeItem) {
        presenter.seItemClicked(seItemTreeItem.getSeItem());
      }
    });

    panelSeItem = new Panel(seItemComponent);
    panelSeItem.setSizeFull();
    panelSeItem.setIcon(FontAwesome.FILE_O);
    panelSeItem.setCaption("SE-Item");
    
    HorizontalLayout hl = new HorizontalLayout(panelSeItems, panelSeItem);
    hl.setExpandRatio(panelSeItems, 1);
    hl.setExpandRatio(panelSeItem, 2);
    hl.setSizeFull();
    hl.setSpacing(true);
    addComponent(hl);
    setExpandRatio(hl, 1);

  }

  @Override
  public void setSeItems(List<SeItem> seItems) {
    seItemsContainer.setSeItems(seItems);
  }

  @Override
  public void setSeItem(SeItem seItem) {
    seItemComponent.setSeItem(seItem.getId().toString());
    panelSeItem.setCaption("SE-Item: " + seItem.getFolder() + seItem.getName());
  }

  @Override
  public void attach() {
    super.attach();
    presenter = new SeItemsPresenter(this);
  }

  @Override
  public void detach() {
    AppEventBus.post(new SearchField.QueryPrefix(""));
    super.detach();
  }

  @Override
  public void enter(ViewChangeEvent event) {
    if (event.getParameters() != null) {
      String[] parameters = event.getParameters()
                                 .split("/");
      if (parameters.length >= 2) {
        String repository = parameters[0];
        String commitId = parameters[1];
        presenter.load(repository, commitId);
        commitInfo.setRepository(repository);
        commitInfo.setCommitId(commitId);
        AppEventBus.post(new AppEvent.TitleChangeEvent("SE-Items"));
        AppEventBus.post(new SearchField.QueryPrefix("repository:" + repository + " AND commitid:" + commitId + " AND "));
      } else {
        Notification.show("Not enough parameters to present the SE-Items.", Type.ERROR_MESSAGE);
      }
    }
  }

}
