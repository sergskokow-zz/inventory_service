package ru.gctc.inventory.server.security;

import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import ru.gctc.inventory.server.vaadin.ui.LoginForm;

public interface RequiresAuthorization extends BeforeEnterObserver {
    default boolean hasRights() {
        return SecurityUtils.isUserLoggedIn();
    }

    @Override
    default void beforeEnter(BeforeEnterEvent event) {
        if(!hasRights())
            event.rerouteTo(LoginForm.class);
    }
}
