package ch.hsr.isf.serepo.client.webapp.view.consistencies.relations;

import java.util.Arrays;
import java.util.List;

import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Label;
import com.vaadin.ui.Table;
import com.vaadin.ui.Table.CellStyleGenerator;
import com.vaadin.ui.Table.ColumnGenerator;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.data.restinterface.consistency.relation.RelationInconsistency;

public class InconsistenciesTable extends CustomComponent {

  private static final long serialVersionUID = -8014481309832762934L;
  
  private Table table;
  private BeanItemContainer<RelationInconsistency> container = new BeanItemContainer<>(RelationInconsistency.class);
  
  public InconsistenciesTable() {
    
    setCaption("Inconsistencies for selected SE-Item");
    
    table = new Table();
    table.setSizeFull();
    table.addStyleName(ValoTheme.TABLE_NO_HEADER);
    table.setContainerDataSource(container, Arrays.asList("inconsistency"));
    table.setCellStyleGenerator(new CellStyleGenerator() {
      private static final long serialVersionUID = -9086401080925860447L;

      @Override
      public String getStyle(Table source, Object itemId, Object propertyId) {
        if ("inconsistency".equals(propertyId)) {
          return "multiline";
        } else {
          return null;
        }
      }
    });
    table.addGeneratedColumn("inconsistency", new ColumnGenerator() {
      private static final long serialVersionUID = -1733707602010816019L;

      @Override
      public Object generateCell(Table source, Object itemId, Object columnId) {
        return new Label(container.getItem(itemId).getBean().getInconsistency());
      }
    });
    table.setColumnHeaders("Inconsistencies");
    table.setSelectable(false);
    
    setSizeFull();
    setCompositionRoot(table);
    
  }

  public void setInconsistencies(List<RelationInconsistency> inconsistencies) {
    container.removeAllItems();
    container.addAll(inconsistencies);
  }
  
}
