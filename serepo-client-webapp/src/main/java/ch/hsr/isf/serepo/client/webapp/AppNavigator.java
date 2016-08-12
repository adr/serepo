package ch.hsr.isf.serepo.client.webapp;

import com.vaadin.navigator.Navigator;
import com.vaadin.navigator.View;
import com.vaadin.navigator.ViewChangeListener;
import com.vaadin.navigator.ViewProvider;
import com.vaadin.ui.ComponentContainer;
import com.vaadin.ui.UI;

import ch.hsr.isf.serepo.client.webapp.view.AppViewType;

public class AppNavigator extends Navigator {

    private static final AppViewType ERROR_VIEW = AppViewType.getHome();
    private ViewProvider errorViewProvider;

    public AppNavigator(final ComponentContainer container) {
        super(UI.getCurrent(), container);
        initViewChangeListener();
        initViewProviders();
    }

	public static void navigateTo(AppViewType view, String...parameters) {
		StringBuilder paramString = new StringBuilder();
		if (parameters != null) {
			for (String s : parameters) {
				paramString.append(s).append("/");
			}
		}
		MyUI.getCurrent().getNavigator().navigateTo(String.format("%s/%s", view.getViewName(), paramString.toString()));
	}

	private void initViewChangeListener() {
        addViewChangeListener(new ViewChangeListener() {
			private static final long serialVersionUID = 948208712194747881L;

			@Override
            public boolean beforeViewChange(final ViewChangeEvent event) {
                // Since there's no conditions in switching between the views
                // we can always return true.
                return true;
            }

            @Override
            public void afterViewChange(final ViewChangeEvent event) {
//                DashboardViewType view = DashboardViewType.getByViewName(event.getViewName());
//                // Appropriate events get fired after the view is changed.
//                DashboardEventBus.post(new PostViewChangeEvent(view));
//                DashboardEventBus.post(new BrowserResizeEvent());
//                DashboardEventBus.post(new CloseOpenWindowsEvent());
            }
        });
    }

    private void initViewProviders() {
        // A dedicated view provider is added for each separate view type
        for (final AppViewType viewType : AppViewType.values()) {
            ViewProvider viewProvider = new ClassBasedViewProvider(viewType.getViewName(), viewType.getViewClass());
            if (viewType == ERROR_VIEW) {
                errorViewProvider = viewProvider;
            }
            addProvider(viewProvider);
        }

        setErrorProvider(new ViewProvider() {
			private static final long serialVersionUID = 7167697095630738938L;

			@Override
            public String getViewName(final String viewAndParameters) {
                return ERROR_VIEW.getViewName();
            }

            @Override
            public View getView(final String viewName) {
                return errorViewProvider.getView(ERROR_VIEW.getViewName());
            }
        });
    }

}
