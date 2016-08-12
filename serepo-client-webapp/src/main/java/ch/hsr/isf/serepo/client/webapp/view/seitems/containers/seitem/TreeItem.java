package ch.hsr.isf.serepo.client.webapp.view.seitems.containers.seitem;

public class TreeItem {

	private Object id;
	private String caption;

	public TreeItem(Object id, String caption) {
		this.id = id;
		this.caption = caption;
	}

	@Override
	public String toString() {
		return caption;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		TreeItem other = (TreeItem) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}
	
}
