package ru.gctc.inventory.server.vaadin.ui;

import com.vaadin.flow.component.login.LoginOverlay;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;

import java.util.Collections;

@Route(value = "login")
@PageTitle("Вход")
public class LoginForm extends VerticalLayout implements BeforeEnterObserver {
    private final LoginOverlay loginOverlay = new LoginOverlay();

    public LoginForm() {
        loginOverlay.setOpened(true);
        loginOverlay.setAction("login");
        loginOverlay.setTitle("Вход");
        loginOverlay.setDescription("Войдите, чтобы редактировать базу данных системы инвентаризации.");
        getElement().appendChild(loginOverlay.getElement());
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        if(!event.getLocation().getQueryParameters().getParameters()
                .getOrDefault("error", Collections.emptyList()).isEmpty()) {
            loginOverlay.setError(true);
        }
    }
}
