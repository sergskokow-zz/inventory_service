package ru.gctc.inventory.server.vaadin.providers;

import lombok.Getter;
import lombok.Setter;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.services.InventoryService;

@Getter @Setter
public class InventoryEntityManager<IE extends InventoryEntity> {
    private IE inventoryEntity;
    private InventoryService<IE> inventoryService;

    public InventoryEntityManager(IE inventoryEntity, InventoryService<IE> inventoryService) {
        this.inventoryEntity = inventoryEntity;
        this.inventoryService = inventoryService;
    }
}
