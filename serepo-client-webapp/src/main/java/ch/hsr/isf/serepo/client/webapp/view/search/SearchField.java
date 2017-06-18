package ch.hsr.isf.serepo.client.webapp.view.search;

import com.vaadin.event.ShortcutAction;
import com.vaadin.event.ShortcutListener;
import com.vaadin.server.FontAwesome;
import com.vaadin.ui.TextField;
import com.vaadin.ui.themes.ValoTheme;

import ch.hsr.isf.serepo.client.webapp.event.AppEventBus;

public class SearchField extends TextField {
  
  private static final long serialVersionUID = 5505716525523405369L;

  public static class SearchRequest {
    private String query;

    public SearchRequest(String query) {
      this.query = query;
    }

    public String getQuery() {
      return query;
    }
  }
  
  public SearchField() {
    setIcon(FontAwesome.SEARCH);
    setInputPrompt("Search for SE-Items...");
    addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
    setNullRepresentation(""); 
    addShortcutListener(new ShortcutListener("search", ShortcutAction.KeyCode.ENTER, null) {
      private static final long serialVersionUID = -1799875565443913232L;

      @Override
      public void handleAction(Object sender, Object target) {
        if (!getValue().isEmpty()) {
          AppEventBus.post(new SearchRequest(getValue()));
        }
      }
    });
  }
  
}
