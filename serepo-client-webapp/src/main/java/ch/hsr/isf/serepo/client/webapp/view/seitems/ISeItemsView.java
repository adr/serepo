package ch.hsr.isf.serepo.client.webapp.view.seitems;

import java.net.URL;
import java.util.List;
import java.util.Map;

import ch.hsr.isf.serepo.data.restinterface.common.Link;
import ch.hsr.isf.serepo.data.restinterface.seitem.SeItem;

public interface ISeItemsView {

	void setSeItems(List<SeItem> seItems);

	void setSeItemContent(String seItemName, URL url);
	
	void setSeItemMetadata(String seItemName, Map<String, Object> metadata);

	void setSeItemRelations(String seItemName, List<Link> relations);
	
}
