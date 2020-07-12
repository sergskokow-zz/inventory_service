package ru.gctc.inventory.server.db.utils;

import ru.gctc.inventory.server.db.entities.*;
import ru.gctc.inventory.server.vaadin.utils.InventoryEntityNames;

import java.util.LinkedList;
import java.util.List;
import java.util.stream.Collectors;

public abstract class ItemPath {
    public static List<InventoryEntity> of(Item item) {// TODO recursive
        LinkedList<InventoryEntity> path = new LinkedList<>();
        Place p = item.getPlace();
        Container c;
        Room r;
        if(p!=null) {
            c = p.getContainer();
            r = c.getRoom();
            path.add(c);
            path.add(p);
        } else
            r = item.getRoom();
        Floor f = r.getFloor();
        Building b = f.getBuilding();
        path.addFirst(r);
        path.addFirst(f);
        path.addFirst(b);
        return path;
    }
    public static String toString(Item item) {
        return of(item).stream().map(InventoryEntityNames::get).collect(Collectors.joining(", "));
    }
}
