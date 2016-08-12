package ch.hsr.isf.serepo.client.webapp.view.repositories;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import com.vaadin.data.fieldgroup.BeanFieldGroup;
import com.vaadin.server.VaadinSession;
import com.vaadin.ui.AbstractField;
import com.vaadin.ui.Alignment;
import com.vaadin.ui.Button;
import com.vaadin.ui.Button.ClickEvent;
import com.vaadin.ui.Button.ClickListener;
import com.vaadin.ui.Field;
import com.vaadin.ui.Notification;
import com.vaadin.ui.Notification.Type;
import com.vaadin.ui.TextArea;
import com.vaadin.ui.TextField;
import com.vaadin.ui.UI;
import com.vaadin.ui.VerticalLayout;
import com.vaadin.ui.Window;

import ch.hsr.isf.serepo.client.webapp.model.Settings;
import ch.hsr.isf.serepo.client.webapp.model.User;
import ch.hsr.isf.serepo.data.restinterface.repository.CreateRepository;

public class CreateRepositoryWindow extends Window {

  private static final long serialVersionUID = 7403420477446187314L;

  private BeanFieldGroup<CreateRepository> fieldGroup;
  private TextField tfRepositoryName;
  private TextArea taDescription;

  private Listener listener;

  public interface Listener {
    void created(String repositoryName);
  }

  public CreateRepositoryWindow() {

    setCaption("Create new repository...");
    setWidth("600px");
    setHeight("450px");
    center();
    setModal(true);

    tfRepositoryName = new TextField("Repository name");
    tfRepositoryName.setRequired(true);
    tfRepositoryName.setRequiredError("This field is required.");
    tfRepositoryName.setWidth("100%");

    taDescription = new TextArea("Description for new repository");
    taDescription.setRequired(true);
    tfRepositoryName.setRequiredError("This field is required.");
    taDescription.setSizeFull();

    User sessionUser = (User) VaadinSession.getCurrent()
                                           .getAttribute(User.class.getName());
    CreateRepository createRepository = new CreateRepository();
    createRepository.setName("");
    createRepository.setDescription("");
    ch.hsr.isf.serepo.data.restinterface.common.User user =
        new ch.hsr.isf.serepo.data.restinterface.common.User(sessionUser.getUsername(),
            sessionUser.getEmail());
    createRepository.setUser(user);

    fieldGroup = new BeanFieldGroup<CreateRepository>(CreateRepository.class);
    fieldGroup.setBuffered(false);
    fieldGroup.setItemDataSource(createRepository);

    fieldGroup.bind(tfRepositoryName, "name");
    fieldGroup.bind(taDescription, "description");

    setValidationVisible(false);
    Button btnCreate = new Button("Create", new ClickListener() {
      private static final long serialVersionUID = -430904124443580740L;

      @Override
      public void buttonClick(ClickEvent event) {
        setValidationVisible(true);
        if (fieldGroup.isValid()) {
          createRepository();
        } else {
          Notification.show("Not all fields are valid!", Type.ERROR_MESSAGE);
        }
      }
    });

    VerticalLayout vl = new VerticalLayout(tfRepositoryName, taDescription, btnCreate);
    vl.setSizeFull();
    vl.setMargin(true);
    vl.setSpacing(true);
    vl.setExpandRatio(taDescription, 1.0f);
    vl.setComponentAlignment(btnCreate, Alignment.MIDDLE_RIGHT);

    setContent(vl);

  }

  private void setValidationVisible(boolean visible) {
    for (Field<?> field : fieldGroup.getFields()) {
      if (AbstractField.class.isInstance(field)) {
        ((AbstractField<?>) field).setValidationVisible(visible);
      }
    }
  }

  public void show() {
    UI.getCurrent()
      .addWindow(this);
  }

  public void setListener(Listener listener) {
    this.listener = listener;
  }

  private void createRepository() {

    Client client = ClientBuilder.newClient();
    WebTarget target = client.target(String.format("%s/repos", Settings.getFromSession()
                                                                       .getSerepoUrl()));
    Response response = null;
    try {
      response = target.request()
                       .post(Entity.entity(fieldGroup.getItemDataSource()
                                                     .getBean(),
                           MediaType.APPLICATION_JSON_TYPE));
      switch (Status.fromStatusCode(response.getStatus())) {
        case CREATED:
          Notification.show("Repository created");
          if (listener != null) {
            listener.created(fieldGroup.getItemDataSource()
                                       .getBean()
                                       .getName());
          }
          close();
          break;
        default:
          Notification.show(response.getStatusInfo()
                                    .getReasonPhrase(),
              response.readEntity(String.class), Type.ERROR_MESSAGE);
          break;
      }
    } finally {
      if (response != null) {
        response.close();
      }
    }

  }

}
