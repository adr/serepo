package ch.hsr.isf.serepo.client.webapp.view;

import com.vaadin.navigator.View;
import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

import ch.hsr.isf.serepo.client.webapp.view.commits.CommitsView;
import ch.hsr.isf.serepo.client.webapp.view.consistencies.relations.RelationConsistencyView;
import ch.hsr.isf.serepo.client.webapp.view.repositories.RepositoriesView;
import ch.hsr.isf.serepo.client.webapp.view.search.SearchView;
import ch.hsr.isf.serepo.client.webapp.view.seitems.SeItemsView;

public enum AppViewType {
//	DASHBOARD("dashboard", DashboardView.class, FontAwesome.HOME, true),
	REPOSITORIES("repositories", RepositoriesView.class, FontAwesome.DATABASE, false), 
	COMMITS("commits", CommitsView.class, FontAwesome.PENCIL_SQUARE_O, false), 
	SEITEMS("seitems", SeItemsView.class, FontAwesome.SITEMAP, false),
	SEARCH("search", SearchView.class, FontAwesome.SEARCH, false),
	CONSISTENCY("consistency", RelationConsistencyView.class, FontAwesome.SEARCH, false);

	private final String viewName;
	private final Class<? extends View> viewClass;
	private final Resource icon;
	private final boolean stateful;

	private AppViewType(final String viewName, final Class<? extends View> viewClass, final Resource icon, final boolean stateful) {
		this.viewName = viewName;
		this.viewClass = viewClass;
		this.icon = icon;
		this.stateful = stateful;
	}

	public boolean isStateful() {
		return stateful;
	}

	public String getViewName() {
		return viewName;
	}

	public Class<? extends View> getViewClass() {
		return viewClass;
	}

	public Resource getIcon() {
		return icon;
	}

	public static AppViewType getByViewName(final String viewName) {
		AppViewType result = null;
		for (AppViewType viewType : values()) {
			if (viewType.getViewName().equals(viewName)) {
				result = viewType;
				break;
			}
		}
		return result;
	}
	
	public static AppViewType getHome() {
		return AppViewType.REPOSITORIES;
	}

}
