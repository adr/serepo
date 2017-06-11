package ch.hsr.isf.serepo.client.webapp.view.seitems.containers.seitem;

import com.vaadin.server.FontAwesome;
import com.vaadin.server.Resource;

public class FolderTreeItem extends TreeItem {

  public static final Resource ICON_CLOSED = FontAwesome.FOLDER_O;
  public static final Resource ICON_OPEN = FontAwesome.FOLDER_OPEN_O;

  public FolderTreeItem(Object id, String path, String caption) {
    super(id, path, caption, ICON_CLOSED);
  }
  
}
