package ch.hsr.isf.serepo.client.webapp.view.seitems;

import java.util.List;

import ch.hsr.isf.serepo.data.restinterface.seitem.SeItem;

public interface ISeItemsView {

	void setSeItems(List<SeItem> seItems);
	
	void setSeItem(SeItem seItem);

}
