package ch.hsr.isf.serepo.client.webapp.view.seitems;

import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.TabSheet;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.client.webapp.services.SeItemDataLoader;
import ch.hsr.isf.serepo.client.webapp.view.seitems.containers.ContentContainer;
import ch.hsr.isf.serepo.client.webapp.view.seitems.containers.MetadataContainer;
import ch.hsr.isf.serepo.client.webapp.view.seitems.containers.RelationsContainer;

public class SeItemComponent extends CustomComponent {

  private static final long serialVersionUID = -5031069964923779030L;
  
  private TabSheet tabSheet = new TabSheet();
  private ContentContainer contentContainer = new ContentContainer();
  private MetadataContainer metadataContainer = new MetadataContainer();
  private RelationsContainer relationsContainer = new RelationsContainer();
  
  public SeItemComponent() {
    
    setSizeFull();
    configureTabSheet();
    addTabs();
    setCompositionRoot(tabSheet);
    
  }
  
  public void setSeItem(String seItemUrl) {
    contentContainer.setContent(seItemUrl);
    metadataContainer.setMetatadata(SeItemDataLoader.loadMetadata(seItemUrl));
    relationsContainer.setRelations(SeItemDataLoader.loadRelations(seItemUrl));
  }
  
  private void configureTabSheet() {
    tabSheet.setSizeFull();
    tabSheet.addStyleName(ValoTheme.TABSHEET_ICONS_ON_TOP);
  }

  private void addTabs() {
    tabSheet.addTab(contentContainer, contentContainer.getCaption(), contentContainer.getIcon());
    tabSheet.addTab(metadataContainer, metadataContainer.getCaption(), metadataContainer.getIcon());
    tabSheet.addTab(relationsContainer, relationsContainer.getCaption(), relationsContainer.getIcon());
  }
  
}
