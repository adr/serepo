package ch.hsr.isf.serepo.client.webapp.view.seitems.containers.seitem;

import com.vaadin.server.FontAwesome;

import ch.hsr.isf.serepo.data.restinterface.seitem.SeItem;

public class SeItemTreeItem extends TreeItem {

  private SeItem seItem;

  public SeItemTreeItem(SeItem seItem) {
    super(seItem.getId(), seItem.getFolder(), seItem.getName(), FontAwesome.FILE_O);
    this.seItem = seItem;
  }

  public SeItem getSeItem() {
    return seItem;
  }

}
