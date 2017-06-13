package ch.hsr.isf.serepo.client.webapp.view.seitems;

import com.vaadin.event.FieldEvents.FocusEvent;
import com.vaadin.event.FieldEvents.FocusListener;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.client.webapp.MyUI;
import ch.hsr.isf.serepo.client.webapp.services.SeItemDataLoader;
import ch.hsr.isf.serepo.client.webapp.view.seitems.containers.ContentContainer;
import ch.hsr.isf.serepo.client.webapp.view.seitems.containers.MetadataContainer;
import ch.hsr.isf.serepo.client.webapp.view.seitems.containers.RelationsContainer;

public class ShowSeItemWindow extends Window {

  private static final long serialVersionUID = 250287259763872613L;
  
  private TextArea seItemUri = new TextArea("URI of SE-Item");
  
  private TabSheet tabSheet = new TabSheet();
  private ContentContainer contentContainer = new ContentContainer();
  private MetadataContainer metadataContainer = new MetadataContainer();
  private RelationsContainer relationsContainer = new RelationsContainer();

  public ShowSeItemWindow(String uriOfSeItem) {

    configureWindow();
    
    configureTabSheet();
    addTabs();
    configureTextArea(uriOfSeItem);
    
    VerticalLayout vlContent = new VerticalLayout(tabSheet, seItemUri);
    vlContent.setSizeFull();
    vlContent.setSpacing(true);
    vlContent.setMargin(true);
    vlContent.setExpandRatio(tabSheet, 1);
    setContent(vlContent);
    
    loadSeItemData(uriOfSeItem);
    open();

  }

  private void configureTextArea(String uri) {
    seItemUri.setWidth("100%");
    seItemUri.setHeight(5, Unit.EM);
    seItemUri.setValue(uri);
    seItemUri.setReadOnly(true);
    seItemUri.addFocusListener(new FocusListener() {
      
      @Override
      public void focus(FocusEvent event) {
        ((TextArea) event.getComponent()).selectAll();
      }
    });
  }
  
  private void loadSeItemData(String seItemUrl) {
    contentContainer.setContent(seItemUrl);
    metadataContainer.setMetatadata(SeItemDataLoader.loadMetadata(seItemUrl));
    relationsContainer.setRelations(SeItemDataLoader.loadRelations(seItemUrl));
  }

  private void configureTabSheet() {
    tabSheet.setSizeFull();
    tabSheet.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP);
  }

  private void addTabs() {
    tabSheet.addTab(contentContainer, "Content", contentContainer.getIcon());
    tabSheet.addTab(metadataContainer, "Metadata", metadataContainer.getIcon());
    tabSheet.addTab(relationsContainer, "Relations", relationsContainer.getIcon());
  }

  private void open() {
    MyUI.getCurrent()
        .addWindow(this);
  }

  private void configureWindow() {
    setCaption("Quick show of SE-Item");
    setHeight("80%");
    setWidth("80%");
    center();
    setModal(true);
    setClosable(true);
  }

}
