package ru.gctc.inventory.server.db.repos;

import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.Building;

@Repository
public interface BuildingRepository extends InventoryRepository<Building> {
    
}
