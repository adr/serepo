package ch.hsr.isf.serepo.client.webapp.view.commits;

import java.util.List;

import ch.hsr.isf.serepo.data.restinterface.commit.Commit;

public interface ICommitsView {

	void setCommits(List<Commit> commits);
	
}
