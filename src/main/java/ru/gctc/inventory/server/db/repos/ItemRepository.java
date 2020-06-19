package ru.gctc.inventory.server.db.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.entities.Place;
import ru.gctc.inventory.server.db.entities.Room;

@Repository
public interface ItemRepository extends InventoryRepository<Item> {

    Page<Item> findByNameContainingIgnoreCase(String name, Pageable pageable);

    Page<Item> findAllByRoom(Room room, Pageable pageable);

    Page<Item> findAllByPlace(Place place, Pageable pageable);

    long countAllByRoom(Room room);

    long countAllByPlace(Place place);

    boolean existsItemByRoom(Room room);

    boolean existsItemByPlace(Place place);
}
