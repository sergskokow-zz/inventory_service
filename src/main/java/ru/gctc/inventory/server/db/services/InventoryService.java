package ru.gctc.inventory.server.db.services;

import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.services.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public interface InventoryService<E extends InventoryEntity> {
    E add(E entity);

    E edit(E entity) throws EntityNotFoundException;

    void delete(E entity);

    void delete(long entityId) throws EntityNotFoundException;

    long count();

    List<E> getAll(int offset, int limit);

    Optional<E> getById(long entityId);

    List<E> getChildren(InventoryEntity parent, int offset, int limit);

    long getChildCount(InventoryEntity parent);

    boolean hasChildren(InventoryEntity parent);

    InventoryEntity getParent(E entity);
}
