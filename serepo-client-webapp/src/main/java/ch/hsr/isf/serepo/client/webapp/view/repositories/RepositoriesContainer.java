package ch.hsr.isf.serepo.client.webapp.view.repositories;

import java.util.List;

import com.google.common.base.Optional;
import com.vaadin.data.util.BeanItemContainer;
import com.vaadin.ui.CustomComponent;
import com.vaadin.ui.Table;

import ch.hsr.isf.serepo.client.webapp.view.UserConverter;
import ch.hsr.isf.serepo.data.restinterface.repository.Repository;

public class RepositoriesContainer extends CustomComponent {

	private static final long serialVersionUID = -2885424912611441094L;

	private final Table table;
	private final BeanItemContainer<Repository> container;
	
	public RepositoriesContainer() {
		
		setSizeFull();
		
		table = new Table(null, container = new BeanItemContainer<>(Repository.class));
		table.setSelectable(true);
		table.setSizeFull();
		table.setVisibleColumns("name", "description", "updated", "lastUpdateUser");
		table.setColumnHeaders("Name", "Description", "Last commit at", "Last commit by");
		table.setConverter("lastUpdateUser", new UserConverter());
		
		setCompositionRoot(table);
		
	}
	
	public void setRepositories(List<Repository> repositories) {
		container.removeAllItems();
		container.addAll(repositories);
	}
	
	public Optional<Repository> getSelectedRepository() {
		return Optional.fromNullable((Repository) table.getValue());
	}

}
