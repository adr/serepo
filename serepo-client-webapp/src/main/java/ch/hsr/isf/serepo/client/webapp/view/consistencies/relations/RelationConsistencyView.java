package ch.hsr.isf.serepo.client.webapp.view.consistencies.relations;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener.ViewChangeEvent;
import com.vaadin.server.FileDownloader;
import com.vaadin.server.StreamResource;
import com.vaadin.server.StreamResource.StreamSource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.client.webapp.event.AppEvent;
import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;
import ch.hsr.isf.serepo.data.restinterface.consistency.relation.RelationInconsistency;

public class RelationConsistencyView extends VerticalLayout implements IRelationConsistencyView, View {

  private static final long serialVersionUID = -1063576922306763013L;

  private static final Logger logger = LoggerFactory.getLogger(RelationConsistencyView.class);
  
  private InconsistenciesTree inconsistenciesTree = new InconsistenciesTree();
  private InconsistenciesTable inconsistenciesTable = new InconsistenciesTable(); 

  private RelationConsistencyPresenter presenter;

  private List<RelationInconsistency> inconsistencies;
  
  public RelationConsistencyView() {
    
    setSizeFull();
    setMargin(false);

    inconsistenciesTree.setListener(new InconsistenciesTree.Listener() {
      
      @Override
      public void seItemClicked(List<RelationInconsistency> inconsistencies) {
        inconsistenciesTable.setInconsistencies(inconsistencies);
      }
      
    });
    
    HorizontalLayout hl = new HorizontalLayout(inconsistenciesTree, inconsistenciesTable);
    addComponent(hl);
    setExpandRatio(hl, 1);
    hl.setSizeFull();
    hl.setSpacing(true);
    
    Button btnCsv = new Button("export as CSV");
    new FileDownloader(new StreamResource(new StreamSource() {
      private static final long serialVersionUID = -8395855807747913797L;

      @Override
      public InputStream getStream() {
        try {
          byte[] exportAsCsv = presenter.exportAsCsv(inconsistencies);
          return new ByteArrayInputStream(exportAsCsv);
        } catch (IOException e) {
          logger.error("There was an error while creating the CSV file.", e);
          Notification.show("An internal error occured while creating the CSV file.", Type.ERROR_MESSAGE);
          return null;
        }
      }
    }, "Inconsistencies.csv")).extend(btnCsv);

    btnCsv.addStyleName(ValoTheme.BUTTON_LINK);
    btnCsv.addStyleName(ValoTheme.BUTTON_SMALL);
    addComponent(btnCsv);
    setComponentAlignment(btnCsv, Alignment.MIDDLE_RIGHT);
    
  }

  @Override
  public void setInconsistencies(List<RelationInconsistency> inconsistencies) {
    this.inconsistencies = inconsistencies;
    sort(inconsistencies);
    inconsistenciesTree.setInconsistencies(inconsistencies);
    inconsistenciesTable.setInconsistencies(new ArrayList<RelationInconsistency>());
  }
  
  @Override
  public void attach() {
    super.attach();
    presenter = new RelationConsistencyPresenter(this);
  }

  @Override
  public void enter(ViewChangeEvent event) {
    String[] parameters = event.getParameters().split("/");
    if (parameters.length != 2) {
      Notification.show("Wrong parameters provided.", Type.ERROR_MESSAGE);
    } else {
      presenter.loadInconsistencies(parameters[0], parameters[1]);
      String title = String.format("Consistency-Check for '%s' - '%s'", parameters[0], parameters[1]);
      AppEventBus.post(new AppEvent.TitleChangeEvent(title));
    }
  }

  private void sort(List<RelationInconsistency> inconsistencies) {
    Collections.sort(inconsistencies, new Comparator<RelationInconsistency>() {
      @Override
      public int compare(RelationInconsistency o1, RelationInconsistency o2) {
        return o1.getSeItem().compareTo(o2.getSeItem());
      }});
  }

}
