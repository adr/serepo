package ch.hsr.isf.serepo.client.webapp.view.seitems;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Component;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.HorizontalLayout;
import com.vaadin.ui.Label;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.client.webapp.AppNavigator;
import ch.hsr.isf.serepo.client.webapp.view.AppViewType;

public class CommitInfoComponent extends CustomComponent {

  private HorizontalLayout layout = new HorizontalLayout() {

    @Override
    public void addComponent(Component c) {
      super.addComponent(c);
      super.setComponentAlignment(c, Alignment.MIDDLE_LEFT);
    }
    
  };
  
  private Label lblRepository = new Label();
  private Label lblCommitId = new Label();
  
  public CommitInfoComponent() {
    createLayout();
    setCompositionRoot(layout);
  }
  
  private void createLayout() {
    layout.setSpacing(true);

    layout.addComponent(createActionButton("Repository:", FontAwesome.DATABASE, false, new ClickListener() {
      
      @Override
      public void buttonClick(ClickEvent event) {
        AppNavigator.navigateTo(AppViewType.REPOSITORIES);
      }
    }));
    layout.addComponent(lblRepository);
    layout.addComponent(createActionButton("Commit-ID:", FontAwesome.CODE_FORK, true, new ClickListener() {
      
      @Override
      public void buttonClick(ClickEvent event) {
        AppNavigator.navigateTo(AppViewType.COMMITS, lblRepository.getValue());
      }
    }));
    layout.addComponent(lblCommitId);
  }
  
  private Button createActionButton(String caption, Resource icon, boolean addSpace, ClickListener action) {
    Button btn = new Button(caption, icon);
    btn.addClickListener(action);
    btn.addStyleName(ValoTheme.BUTTON_BORDERLESS);
    btn.addStyleName(ValoTheme.BUTTON_LINK);
    btn.addStyleName(ValoTheme.BUTTON_SMALL);
    if (addSpace) {
      btn.addStyleName("left-space-30");
    }
    return btn;
  }
  
  public void setRepository(String repository) {
    lblRepository.setValue(repository);
  }
  
  public void setCommitId(String commitId) {
    lblCommitId.setValue(commitId);
  }
  
}
