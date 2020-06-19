package ru.gctc.inventory.server.db.services;

import org.springframework.transaction.annotation.Transactional;
import ru.gctc.inventory.server.db.entities.ContainsItems;
import ru.gctc.inventory.server.db.entities.Item;

import java.util.List;
import java.util.stream.Collectors;

public interface ContainsItemsEntityService<CI extends ContainsItems> extends InventoryService<CI> {

    int itemCount(long entityId);

    List<Item> getAllItems(long entityId);

    @Transactional
    default List<Item> getAllItems(long entityId, int offset, int limit) {
        return /* ?! */ getAllItems(entityId)
                .stream()
                .skip(offset)
                .limit(limit)
                .collect(Collectors.toList());
    }
}
