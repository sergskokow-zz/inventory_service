package ru.gctc.inventory.server.security;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import ru.gctc.inventory.server.vaadin.ui.LoginForm;

public interface RequiresRole extends BeforeEnterObserver {
    default boolean hasRights(Class<?> navigationTarget) {
        return SecurityUtils.isUserLoggedIn() && SecurityUtils.isAccessGranted(navigationTarget);
    }

    @Override
    default void beforeEnter(BeforeEnterEvent event) {
        if(!hasRights(event.getNavigationTarget()))
            event.rerouteTo(LoginForm.class);
    }
}
