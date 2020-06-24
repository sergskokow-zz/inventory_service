package ru.gctc.inventory.server.vaadin.ui;

import com.vaadin.flow.component.icon.VaadinIcon;
import com.vaadin.flow.spring.annotation.UIScope;
import de.codecamp.vaadin.components.messagedialog.MessageDialog;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.*;
import ru.gctc.inventory.server.db.services.*;
import ru.gctc.inventory.server.vaadin.utils.InventoryEntityNames;

import java.util.Arrays;
import java.util.Set;

@UIScope
@Component
public class DeleteConfirmDialog extends MessageDialog {
    private final BuildingService buildingService;
    private final FloorService floorService;
    private final RoomService roomService;
    private final ContainerService containerService;
    private final PlaceService placeService;
    private final ItemService itemService;

    private final FluentButton okButton;

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
        okButton = addButton().text("Удалить").error().icon(VaadinIcon.TRASH).closeOnClick();
        addButton().text("Отмена").primary().closeOnClick();
    }

    public void show(Set<? extends InventoryEntity> entities) {
        if(entities.isEmpty())
            return;

        setMessage(String.format("Будут удалены: %s. Удалить выбранные элементы?",
                Arrays.toString(entities.stream().map(InventoryEntityNames::get).toArray())));

        okButton.onClick(event -> {
            InventoryEntity first = entities.iterator().next();
            if(first instanceof Building)
                entities.stream().map(e -> (Building)e).forEach(buildingService::delete);
            else if(first instanceof Floor)
                entities.stream().map(e -> (Floor)e).forEach(floorService::delete);
            else if(first instanceof Room)
                entities.stream().map(e -> (Room)e).forEach(roomService::delete);
            else if(first instanceof Container)
                entities.stream().map(e -> (Container)e).forEach(containerService::delete);
            else if(first instanceof Place)
                entities.stream().map(e -> (Place)e).forEach(placeService::delete);
            else if(first instanceof Item)
                entities.stream().map(e -> (Item)e).forEach(itemService::delete);
        });

        open();
    }
}
