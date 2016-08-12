package ch.hsr.isf.serepo.client.webapp.view.seitems.containers.seitem;

public class FolderTreeItem extends TreeItem {

	private String path;

	public FolderTreeItem(Object id, String caption, String path) {
		super(id, caption);
		this.path = path;
	}

	public String getPath() {
		return path;
	}
	
}
