package ch.hsr.isf.serepo.client.webapp.view.seitems.containers.seitem;

import ch.hsr.isf.serepo.data.restinterface.seitem.SeItem;

public class SeItemTreeItem extends FolderTreeItem {

	private SeItem seItem;

	public SeItemTreeItem(SeItem seItem) {
		super(String.format("%s/%s", seItem.getFolder(), seItem.getName()), seItem.getName(), seItem.getFolder());
		this.seItem = seItem;
	}

	public SeItem getSeItem() {
		return seItem;
	}

}
