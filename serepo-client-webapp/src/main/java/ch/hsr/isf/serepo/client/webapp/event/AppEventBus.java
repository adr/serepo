package ch.hsr.isf.serepo.client.webapp.event;

import com.google.common.eventbus.EventBus;
import com.google.common.eventbus.SubscriberExceptionContext;
import com.google.common.eventbus.SubscriberExceptionHandler;

import ch.hsr.isf.serepo.client.webapp.MyUI;

public class AppEventBus implements SubscriberExceptionHandler {

    private final EventBus eventBus = new EventBus(this);

    public static void post(Object event) {
        MyUI.getAppEventbus().eventBus.post(event);
    }

    public static void register(Object object) {
    	MyUI.getAppEventbus().eventBus.register(object);
    }

    public static void unregister(Object object) {
    	MyUI.getAppEventbus().eventBus.unregister(object);
    }

    @Override
    public void handleException(Throwable exception, SubscriberExceptionContext context) {
        exception.printStackTrace();
    }

}
