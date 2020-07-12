package ru.gctc.inventory.server.vaadin.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.html.Span;
import com.vaadin.flow.component.icon.Icon;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.orderedlayout.FlexComponent;
import com.vaadin.flow.component.orderedlayout.FlexLayout;
import com.vaadin.flow.component.orderedlayout.HorizontalLayout;
import com.vaadin.flow.component.tabs.Tab;
import com.vaadin.flow.component.tabs.Tabs;
import com.vaadin.flow.router.BeforeEnterEvent;
import com.vaadin.flow.router.BeforeEnterObserver;
import com.vaadin.flow.router.RouterLink;
import com.vaadin.flow.server.PWA;
import ru.gctc.inventory.server.security.SecurityUtils;

import java.util.HashMap;
import java.util.Map;

@PWA(name = "Система инвентаризации", shortName = "Инвентаризация")
public class MainView extends AppLayout implements BeforeEnterObserver {
    private final Tabs tabs = new Tabs();
    private final Map<Class<? extends Component>, Tab> navigationTargetToTab = new HashMap<>();
    private final RouterLink loginButton, logoutButton;

    public MainView() {
        /* Menu tabs */
        addMenuTab("Инвентарь", Inventory.class, VaadinIcon.DATABASE.create());
        addMenuTab("Списание", WriteoffItems.class, VaadinIcon.HOURGLASS_END.create());
        addMenuTab("Параметры", UserPanel.class, VaadinIcon.USER_CARD.create());
        addMenuTab("Администрирование", AdminPanel.class, VaadinIcon.DASHBOARD.create());
        FlexLayout tabsLayout = new FlexLayout();
        tabsLayout.setSizeFull();
        tabsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        tabsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        tabsLayout.add(tabs);
        loginButton = new RouterLink(null, LoginForm.class);
        loginButton.add(new Button("Вход", VaadinIcon.SIGN_IN.create()));
        logoutButton = new RouterLink(null, Logout.class);
        logoutButton.add(new Button("Выход", VaadinIcon.SIGN_OUT.create()));
        tabsLayout.add(loginButton, logoutButton);

        addToNavbar(true, tabsLayout);
    }

    private void addMenuTab(String label, Class<? extends Component> target, Icon icon) {
        RouterLink tabLink = new RouterLink(null, target);
        HorizontalLayout tabLayout = new HorizontalLayout();
        tabLayout.add(icon);
        tabLayout.addAndExpand(new Span(label));
        tabLink.add(tabLayout);
        Tab tab = new Tab(tabLink);
        navigationTargetToTab.put(target, tab);
        tabs.add(tab);
    }

    @Override
    public void beforeEnter(BeforeEnterEvent event) {
        boolean isUserLoggedIn = SecurityUtils.isUserLoggedIn();
        loginButton.setVisible(!isUserLoggedIn);
        logoutButton.setVisible(isUserLoggedIn);
        navigationTargetToTab.get(UserPanel.class).setVisible(isUserLoggedIn);
        navigationTargetToTab.get(AdminPanel.class).setVisible(SecurityUtils.isAccessGranted(AdminPanel.class));

        tabs.setSelectedTab(navigationTargetToTab.get(event.getNavigationTarget()));
    }
}
