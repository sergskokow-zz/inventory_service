package ru.gctc.inventory.server.db.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.Container;
import ru.gctc.inventory.server.db.entities.Place;

@Repository
public interface PlaceRepository extends InventoryRepository<Place> {
    Page<Place> findAllByContainer(Container container, Pageable pageable);

    long countAllByContainer(Container container);

    boolean existsPlaceByContainer(Container container);
}
