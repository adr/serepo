package ch.hsr.isf.serepo.client.webapp.view.search;

import com.google.common.base.Strings;
import com.google.common.eventbus.Subscribe;
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
  
  public static class QueryPrefix {
    private String prefix;
    
    public QueryPrefix(String prefix) {
      this.prefix = prefix;
    }

    public String getPrefix() {
      return prefix;
    }
  }
  
  private String queryPrefix = "";
  private boolean clearAfterSearch = false;
  
  final private ShortcutListener shortcutListener = new ShortcutListener("search", ShortcutAction.KeyCode.ENTER, null) {
    private static final long serialVersionUID = -1799875565443913232L;

    @Override
    public void handleAction(Object sender, Object target) {
      if (!getValue().isEmpty()) {
        AppEventBus.post(new SearchRequest(queryPrefix + getValue()));
        if (clearAfterSearch) {
          setValue("");
        }
      }
    }
  };

  
  public SearchField() {
    setWidth("300px");
    setIcon(FontAwesome.SEARCH);
    setInputPrompt("Search for SE-Items...");
    addStyleName(ValoTheme.TEXTFIELD_INLINE_ICON);
    setNullRepresentation("");
    addShortcutListener(shortcutListener);
  }
  
  public void setClearAfterSearch(boolean clearAfterSearch) {
    this.clearAfterSearch = clearAfterSearch;
  }
  
  @Subscribe
  public void setQueryPrefix(QueryPrefix queryPrefix) {
    this.queryPrefix = addSpaceAtEndIfNeeded(Strings.nullToEmpty(queryPrefix.getPrefix()));
  }
  
  private String addSpaceAtEndIfNeeded(String input) {
    if (!input.isEmpty() && !input.endsWith(" ")) {
      return input + " ";
    }
    return input;
  }

  @Override
  public void attach() {
    super.attach();
    AppEventBus.register(this);
  }

  @Override
  public void detach() {
    AppEventBus.unregister(this);
    super.detach();
  }
  
}
