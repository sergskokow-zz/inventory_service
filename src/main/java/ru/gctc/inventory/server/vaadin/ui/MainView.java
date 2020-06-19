package ru.gctc.inventory.server.vaadin.ui;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.applayout.AppLayout;
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

import java.util.HashMap;
import java.util.Map;

public class MainView extends AppLayout implements BeforeEnterObserver {
    private final Tabs tabs = new Tabs();
    private final Map<Class<? extends Component>, Tab> navigationTargetToTab = new HashMap<>();

    public MainView() {
        /* Menu tabs */
        addMenuTab("Инвентарь", Inventory.class, new Icon(VaadinIcon.DATABASE));
        addMenuTab("Уведомления", Notifications.class, new Icon(VaadinIcon.BELL));
        FlexLayout tabsLayout = new FlexLayout();
        tabsLayout.setSizeFull();
        tabsLayout.setJustifyContentMode(FlexComponent.JustifyContentMode.CENTER);
        tabsLayout.setAlignItems(FlexComponent.Alignment.CENTER);
        tabsLayout.add(tabs);
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
        tabs.setSelectedTab(navigationTargetToTab.get(event.getNavigationTarget()));
    }
}
