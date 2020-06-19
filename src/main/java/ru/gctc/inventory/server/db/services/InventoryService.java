package ru.gctc.inventory.server.db.services;

import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.services.exceptions.EntityAlreadyExistsException;
import ru.gctc.inventory.server.db.services.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface InventoryService<IE extends InventoryEntity> {
    IE add(IE inventoryEntity) throws EntityAlreadyExistsException;

    IE edit(IE inventoryEntity) throws EntityNotFoundException;

    void delete(IE inventoryEntity);

    void delete(long inventoryEntityId) throws EntityNotFoundException;

    long count();

    List<IE> getAll(int offset, int limit);

    Optional<IE> getById(long inventoryEntityId);

    List<? extends InventoryEntity> getChildren(IE inventoryEntity, int offset, int limit);

    default List<? extends InventoryEntity> getChildren(long inventoryEntityId, int offset, int limit) throws EntityNotFoundException {
        return getChildren(getById(inventoryEntityId).orElseThrow(), offset, limit);
    }

    long getChildCount(IE inventoryEntity);

    default long getChildCount(long inventoryEntityId) throws EntityNotFoundException {
        return getChildCount(getById(inventoryEntityId).orElseThrow()); //TODO exception
    }

    boolean hasChildren(IE inventoryEntity);

    default boolean hasChildren(long inventoryEntityId) throws EntityNotFoundException {
        return hasChildren(getById(inventoryEntityId).orElseThrow()); // TODO exception
    }

    Optional<InventoryEntity> getParent(IE inventoryEntity);

    default Optional<InventoryEntity> getParent(long inventoryEntityId) throws EntityNotFoundException {
        return getParent(getById(inventoryEntityId).orElseThrow()); // TODO exception
    }
}
