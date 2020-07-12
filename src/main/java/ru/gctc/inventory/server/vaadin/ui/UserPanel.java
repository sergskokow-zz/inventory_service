package ru.gctc.inventory.server.vaadin.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import ru.gctc.inventory.server.security.RequiresAuthorization;

@Route(value = "user",layout = MainView.class)
@PageTitle("Настройки пользователя")
public class UserPanel extends Div implements RequiresAuthorization {
    public UserPanel() {
        add(new Span("Настройки аккаунта пользователя"));
    }
}
