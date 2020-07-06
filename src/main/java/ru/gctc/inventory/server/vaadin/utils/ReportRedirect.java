package ru.gctc.inventory.server.vaadin.utils;

import com.vaadin.flow.component.page.Page;
import ru.gctc.inventory.server.db.entities.*;

import java.util.Set;
import java.util.stream.Collectors;

public class ReportRedirect {
    public static void reportByParent(Page target, String reportType, InventoryEntity parent) {
        int parentType;
        if(parent instanceof Building)
            parentType = 0;
        else if(parent instanceof Floor)
            parentType = 1;
        else if(parent instanceof Room)
            parentType = 2;
        else if(parent instanceof Container)
            parentType = 3;
        else
            parentType = 4;
        target.open(String.format(
                "download/report/by_parent?type=%s&parentType=%d&parentId=%d", reportType, parentType, parent.getId()));
    }
    public static void reportByItems(Page target, String reportType, Set<Item> items) {
        target.open(String.format("download/report?type=%s&itemsId=%s", reportType,
                items.stream()
                        .map(item -> item.getId().toString())
                        .collect(Collectors.joining(","))));
    }
}
