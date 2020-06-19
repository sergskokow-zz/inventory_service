package ru.gctc.inventory.server.db.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.Item;

@Repository
public interface ItemRepository extends InventoryRepository<Item> {

    Page<Item> findByNameContainingIgnoreCase(String name, Pageable pageable);

}
