package ru.gctc.inventory.server.vaadin.ui;

import com.vaadin.flow.component.html.Div;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.router.PageTitle;
import com.vaadin.flow.router.Route;
import org.springframework.security.access.annotation.Secured;
import ru.gctc.inventory.server.security.RequiresRole;

@Route(value = "admin", layout = MainView.class)
@Secured("ADMINISTRATOR")
@PageTitle("Администрирование")
public class AdminPanel extends Div implements RequiresRole {
    public AdminPanel() {
        add(new Span("Панель администратора"));
    }

}
