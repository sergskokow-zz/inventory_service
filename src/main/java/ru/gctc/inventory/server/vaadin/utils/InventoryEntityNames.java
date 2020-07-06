package ru.gctc.inventory.server.vaadin.utils;

import ru.gctc.inventory.server.db.entities.*;

import java.util.Map;

public class InventoryEntityNames {
    public static String get(InventoryEntity entity) {
        if(entity instanceof Building)
            return ((Building) entity).getName();
        else if(entity instanceof Floor)
            return "Этаж №" + ((Floor) entity).getNumber();
        else if(entity instanceof Room) {
            Room r = (Room) entity;
            return String.format("Кабинет №%d%s",
                    r.getNumber(),
                    r.getName()==null?"":" - "+r.getName());
        }
        else if(entity instanceof Container) {
            Container c = (Container) entity;
            return String.format("%s №%d%s",
                    c.getType()==Container.Type.CASE?"Шкаф":"Стеллаж",
                    c.getNumber(),
                    c.getDescription()==null?"":" - "+c.getDescription());
        }
        else if(entity instanceof Place) {
            Place p = (Place) entity;
            return String.format("%s №%d%s",
                    p.getType()==Place.Type.SHELF?"Полка":"Позиция",
                    p.getNumber(),
                    p.getName()==null?"":" - "+p.getName());
        }
        else if(entity instanceof Item)
            return ((Item) entity).getName();
        return null;
    }

    public static final Map<Class<? extends InventoryEntity>, String> entityTypes = Map.of(
            Building.class, "Здание",
            Floor.class,    "Этаж",
            Room.class,     "Кабинет",
            Container.class,"Шкаф/стеллаж",
            Place.class,    "Полка/позиция",
            Item.class,     "Объект"
    );

    public static final Map<Container.Type, String> containerTypes = Map.of(
            Container.Type.CASE, "Шкаф",
            Container.Type.RACK, "Стеллаж"
    );

    public static final Map<Place.Type, String> placeTypes = Map.of(
            Place.Type.SHELF,   "Полка",
            Place.Type.POSITION,"Позиция"
    );

    public static final Map<Item.Status, String> itemStatus = Map.of(
            Item.Status.IN_USE, "В эксплуатации",
            Item.Status.WRITTEN_OFF, "Списано",
            Item.Status.TRANSFERRED, "Передано на отв. хранение"
    );
}
