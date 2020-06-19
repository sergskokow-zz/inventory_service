package ru.gctc.inventory.server.db.services;

import ru.gctc.inventory.server.db.entities.ContainsItems;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.services.exceptions.EntityNotFoundException;

import java.util.List;

public interface ContainsItemsEntityService<CI extends ContainsItems> extends InventoryService<CI> {

    long itemCount(CI entity);

    default long itemCount(long entityId) throws EntityNotFoundException {
        return itemCount(getById(entityId).orElseThrow());
    }

    List<Item> getAllItems(CI entity, int offset, int limit);

    default List<Item> getAllItems(long entityId, int offset, int limit) throws EntityNotFoundException {
        return getAllItems(getById(entityId).orElseThrow(), offset, limit);
    }

}
