package ru.gctc.inventory.server.vaadin.utils;

import com.vaadin.flow.component.Component;
import com.vaadin.flow.component.page.Page;
import ru.gctc.inventory.server.db.entities.*;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public abstract class DownloadRedirect {
    private static Page getPage(Component component) {
        return component.getUI().get().getPage();
    }

    private static final List<Class<? extends InventoryEntity>> parentTypes = List.of(
            Building.class, Floor.class, Room.class, Container.class, Place.class
    );

    public static void reportByParent(Component from, String reportType, InventoryEntity parent) {
        getPage(from).open(String.format(
                "download/report/by_parent?type=%s&parentType=%d&parentId=%d",
                reportType,
                parentTypes.indexOf(parent.getClass()),
                parent.getId()));
    }

    public static void reportByItems(Component from, String reportType, Set<Item> items) {
        getPage(from).open(String.format("download/report?type=%s&itemsId=%s", reportType,
                items.stream()
                        .map(item -> item.getId().toString())
                        .collect(Collectors.joining(","))));
    }

    public static void qrCodesByParent(Component from, InventoryEntity parent) {
        getPage(from).open(String.format(
                "download/qr/by_parent?parentType=%d&parentId=%d",
                parentTypes.indexOf(parent.getClass()),
                parent.getId()));
    }

    public static void qrCodesByItems(Component from, Set<Item> items) {
        getPage(from).open(String.format("download/qr?itemsId=%s",
                items.stream()
                        .map(item -> item.getId().toString())
                        .collect(Collectors.joining(","))));
    }
}
