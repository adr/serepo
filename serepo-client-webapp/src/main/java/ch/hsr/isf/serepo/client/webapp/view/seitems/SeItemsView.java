package ch.hsr.isf.serepo.client.webapp.view.seitems;

import java.net.URL;
import java.util.List;
import java.util.Map;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;

import ch.hsr.isf.serepo.client.webapp.event.AppEvent;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;
import ch.hsr.isf.serepo.client.webapp.view.seitems.containers.ContentContainer;
import ch.hsr.isf.serepo.client.webapp.view.seitems.containers.MetadataContainer;
import ch.hsr.isf.serepo.client.webapp.view.seitems.containers.RelationsContainer;
import ch.hsr.isf.serepo.client.webapp.view.seitems.containers.seitem.SeItemTreeContainer;
import ch.hsr.isf.serepo.client.webapp.view.seitems.containers.seitem.SeItemTreeItem;
import ch.hsr.isf.serepo.data.restinterface.common.Link;
import ch.hsr.isf.serepo.data.restinterface.seitem.SeItem;

public class SeItemsView extends VerticalLayout implements View, ISeItemsView {

  private static final long serialVersionUID = 1746669287835108355L;

  private SeItemsPresenter presenter;

  private final CommitInfoComponent commitInfo = new CommitInfoComponent();
  private final SeItemTreeContainer seItemsContainer = new SeItemTreeContainer();
  private final ContentContainer contentContainer = new ContentContainer();
  private final MetadataContainer metadataContainer = new MetadataContainer();
  private final RelationsContainer relationsContainer = new RelationsContainer();

  public SeItemsView() {

    setSizeFull();

    addComponent(commitInfo);
    
    seItemsContainer.setCaption("SE-Items");
    seItemsContainer.setListener(new SeItemTreeContainer.Listener() {

      @Override
      public void seItemClicked(SeItemTreeItem seItemTreeItem) {
        presenter.seItemClicked(seItemTreeItem.getSeItem());
      }
    });

    contentContainer.setCaption("Content");
    metadataContainer.setCaption("Metadata");
    relationsContainer.setCaption("Relations");
    
    VerticalLayout vlRight =
        new VerticalLayout(contentContainer, metadataContainer, relationsContainer);
    vlRight.setSizeFull();
    vlRight.setSpacing(true);

    HorizontalLayout hl = new HorizontalLayout(seItemsContainer, vlRight);
    hl.setExpandRatio(seItemsContainer, 1);
    hl.setExpandRatio(vlRight, 2);
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
  public void setSeItemContent(String seItemName, URL url) {
    contentContainer.setCaption(String.format("Content of SE-Item '%s'", seItemName));
    contentContainer.setContent(url);
  }

  @Override
  public void setSeItemMetadata(String seItemName, Map<String, Object> metadata) {
    metadataContainer.setCaption(String.format("Metadata of SE-Item '%s'", seItemName));
    metadataContainer.setMetatadata(metadata);
  }

  @Override
  public void setSeItemRelations(String seItemName, List<Link> relations) {
    relationsContainer.setCaption(String.format("Relations for SE-Item '%s'", seItemName));
    relationsContainer.setRelations(relations);
  }

  @Override
  public void attach() {
    super.attach();
    presenter = new SeItemsPresenter(this);
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
        AppEventBus.post(new AppEvent.TitleChangeEvent("SE-Items"));
        commitInfo.setRepository(repository);
        commitInfo.setCommitId(commitId);
      } else {
        Notification.show("Not enough parameters to present the SE-Items.", Type.ERROR_MESSAGE);
      }
    }
  }

}
