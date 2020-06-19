package ru.gctc.inventory.server.db.services;

import ru.gctc.inventory.server.db.entities.InventoryEntity;

import java.util.List;
import java.util.Optional;

public interface InventoryService<IE extends InventoryEntity> {
    IE add(IE inventoryEntity);
    IE edit(IE inventoryEntity);
    void delete(IE inventoryEntity); // TODO exceptions
    void delete(long inventoryEntityId);
    long count();
    List<IE> getAll();
    Optional<IE> getById(long inventoryEntityId);
    List<? extends InventoryEntity> getChildren(IE inventoryEntity);
    List<? extends InventoryEntity> getChildren(long inventoryEntityId);
    int getChildCount(IE inventoryEntity);
    int getChildCount(long inventoryEntityId);
    boolean hasChildren(IE inventoryEntity);
    boolean hasChildren(long inventoryEntityId);
    Optional<InventoryEntity> getParent(IE inventoryEntity);
    Optional<InventoryEntity> getParent(long inventoryEntityId);
}
