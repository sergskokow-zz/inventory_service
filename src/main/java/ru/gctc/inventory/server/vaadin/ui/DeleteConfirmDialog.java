package ru.gctc.inventory.server.vaadin.ui;

import com.vaadin.flow.component.ClickEvent;
import com.vaadin.flow.component.ComponentEventListener;
import com.vaadin.flow.component.button.Button;
import com.vaadin.flow.component.grid.Grid;
import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.component.treegrid.TreeGrid;
import com.vaadin.flow.spring.annotation.UIScope;
import de.codecamp.vaadin.components.messagedialog.MessageDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.*;
import ru.gctc.inventory.server.db.services.*;
import ru.gctc.inventory.server.security.RequiresAuthorization;
import ru.gctc.inventory.server.vaadin.utils.InventoryEntityNames;

import java.util.Arrays;
import java.util.Set;

@UIScope
@Component
public class DeleteConfirmDialog extends MessageDialog implements RequiresAuthorization {
    private final BuildingService buildingService;
    private final FloorService floorService;
    private final RoomService roomService;
    private final ContainerService containerService;
    private final PlaceService placeService;
    private final ItemService itemService;

    @Autowired
    public DeleteConfirmDialog(BuildingService buildingService,
                               FloorService floorService,
                               RoomService roomService,
                               ContainerService containerService,
                               PlaceService placeService,
                               ItemService itemService) {
        this.buildingService = buildingService;
        this.floorService = floorService;
        this.roomService = roomService;
        this.containerService = containerService;
        this.placeService = placeService;
        this.itemService = itemService;

        setTitle("Удаление", VaadinIcon.TRASH.create());
        listener = new OkButtonListener();
        addButton().text("Удалить").error().icon(VaadinIcon.TRASH).onClick(listener).closeOnClick();
        addButton().text("Отмена").primary().closeOnClick();
    }

    private final OkButtonListener listener;

    public void show(Set<? extends InventoryEntity> entities, TreeGrid<InventoryEntity> tree, Grid<Item> grid) {
        if (entities.isEmpty() || !hasRights())
            return;

        setMessage(String.format("Будут удалены: %s. Удалить выбранные элементы?",
                Arrays.toString(entities.stream().map(InventoryEntityNames::get).toArray())));

        listener.set(entities, tree, grid);
        open();
    }

    private class OkButtonListener implements ComponentEventListener<ClickEvent<Button>> {
        private TreeGrid<InventoryEntity> tree;
        private Grid<Item> grid;

        public void set(Set<? extends InventoryEntity> entities, TreeGrid<InventoryEntity> tree, Grid<Item> grid) {
            this.entities = entities;
            this.tree = tree;
            this.grid = grid;
        }

        private Set<? extends InventoryEntity> entities;

        @Override
        public void onComponentEvent(ClickEvent<Button> event) {
            InventoryEntity first = entities.iterator().next();
            if (first instanceof Building) {
                entities.stream().map(e -> (Building) e).forEach(buildingService::delete);
            }
            else if (first instanceof Floor)
                entities.stream().map(e -> (Floor) e).forEach(floorService::delete);
            else if (first instanceof Room)
                entities.stream().map(e -> (Room) e).forEach(roomService::delete);
            else if (first instanceof Container)
                entities.stream().map(e -> (Container) e).forEach(containerService::delete);
            else if (first instanceof Place)
                entities.stream().map(e -> (Place) e).forEach(placeService::delete);
            else if (first instanceof Item)
                entities.stream().map(e -> (Item) e).forEach(itemService::delete);

            /*if(first instanceof Item)
                grid.getDataProvider().refreshItem((Item) first);
            else if(tree!=null && first instanceof Building)
                tree.getDataProvider().refreshAll();
            else if(tree!=null)
                tree.getDataProvider().refreshItem(first, true);*/
            if(first instanceof Item)
                grid.getDataProvider().refreshAll();
            else if(tree!=null)
                tree.getDataProvider().refreshAll();
        }
    }
}
