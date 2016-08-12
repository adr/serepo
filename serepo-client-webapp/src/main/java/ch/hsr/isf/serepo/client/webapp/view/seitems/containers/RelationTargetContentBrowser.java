package ch.hsr.isf.serepo.client.webapp.view.seitems.containers;

import com.vaadin.ui.TextArea;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import ch.hsr.isf.serepo.client.webapp.MyUI;

public class RelationTargetContentBrowser extends Window {

  private static final long serialVersionUID = 250287259763872613L;
  
  private TextArea taUri = new TextArea("Target URI");
  private ContentContainer contentContainer = new ContentContainer();

  public RelationTargetContentBrowser(String targetType, String uri) {

    setCaption(targetType);
    setHeight("80%");
    setWidth("80%");
    center();
    setModal(true);
    setClosable(true);
    MyUI.getCurrent()
        .addWindow(this);

    taUri.setWidth("100%");
    taUri.setHeight(5, Unit.EM);
    taUri.setValue(uri);

    contentContainer.setCaption("Content of target URI");
    contentContainer.setContent(uri);
    contentContainer.setSizeFull();

    VerticalLayout vl = new VerticalLayout(taUri, contentContainer);
    vl.setMargin(true);
    vl.setSpacing(true);
    vl.setExpandRatio(contentContainer, 1);
    vl.setSizeFull();
    setContent(vl);

  }

}
