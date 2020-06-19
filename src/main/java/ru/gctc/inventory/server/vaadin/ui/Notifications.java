package ru.gctc.inventory.server.vaadin.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.Route;

@Route(value = "notifications", layout = MainView.class)
public class Notifications extends Div {
    public Notifications() {
        add(new Span("Уведомления..."));
    }
}
