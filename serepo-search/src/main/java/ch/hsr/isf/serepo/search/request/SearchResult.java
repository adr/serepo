package ch.hsr.isf.serepo.search.request;

import ch.hsr.isf.serepo.search.SeItemDocumentType;

public class SearchResult {

	private String id;
	private String repository;
	private String commitId;
	private SeItemDocumentType documentType;
	
	public SearchResult() {
	}

	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getRepository() {
		return repository;
	}

	public void setRepository(String repository) {
		this.repository = repository;
	}

	public String getCommitId() {
		return commitId;
	}

	public void setCommitId(String commitId) {
		this.commitId = commitId;
	}

  public SeItemDocumentType getDocumentType() {
    return documentType;
  }

  public void setDocumentType(SeItemDocumentType documentType) {
    this.documentType = documentType;
  }

}
