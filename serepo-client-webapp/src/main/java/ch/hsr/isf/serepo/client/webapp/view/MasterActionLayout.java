package ch.hsr.isf.serepo.client.webapp.view;

import java.util.Arrays;
import java.util.Collection;

import com.vaadin.ui.Alignment;
import com.vaadin.ui.Component;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.VerticalLayout;

public class MasterActionLayout extends HorizontalLayout {

  private static final long serialVersionUID = -9207212879657980369L;

  protected MasterActionLayout() {
    setSpacing(true);
    setSizeFull();
  }

  public MasterActionLayout(Component master, Collection<? extends Component> actions) {
    this(master, actions.toArray(new Component[actions.size()]));
  }

  public MasterActionLayout(Component master, Component... actions) {
    this();
    setMasterComponent(master);
    setActionComponents(Arrays.asList(actions));
  }
  
  protected void setMasterComponent(Component master) {
    addComponent(master);
    setComponentAlignment(master, Alignment.TOP_LEFT);
    setExpandRatio(master, 1);
  }
  
  protected void setActionComponents(Collection<? extends Component> actions) {
    VerticalLayout vlActions = new VerticalLayout();
    vlActions.setWidth("300px");
    vlActions.setSpacing(true);
    addComponent(vlActions);
    setComponentAlignment(vlActions, Alignment.TOP_RIGHT);
    for (Component component : actions) {
      component.setWidth("100%");
      vlActions.addComponent(component);
    }
  }

}
