package ru.gctc.inventory.server.vaadin.ui;

import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.orderedlayout.VerticalLayout;
import com.vaadin.flow.router.Route;

@Route("logout")
public class Logout extends VerticalLayout {
    public Logout() {
        add(new Span("Выход из аккаунта. Перенаправление..."));
    }
}
