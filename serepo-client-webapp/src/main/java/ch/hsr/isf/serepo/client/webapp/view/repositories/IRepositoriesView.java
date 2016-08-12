package ch.hsr.isf.serepo.client.webapp.view.repositories;

import java.util.List;

import ch.hsr.isf.serepo.data.restinterface.repository.Repository;

public interface IRepositoriesView {
	
	void setRepositories(List<Repository> repositories);

}
