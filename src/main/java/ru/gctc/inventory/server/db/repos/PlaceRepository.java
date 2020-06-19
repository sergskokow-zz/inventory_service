package ru.gctc.inventory.server.db.repos;

import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.Place;

@Repository
public interface PlaceRepository extends InventoryRepository<Place> {

}
