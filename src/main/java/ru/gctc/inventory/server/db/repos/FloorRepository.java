package ru.gctc.inventory.server.db.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.Floor;
import ru.gctc.inventory.server.db.entities.InventoryEntity;

@Repository
public interface FloorRepository extends InventoryRepository<Floor> {
    Page<Floor> findAllByBuilding(InventoryEntity building, Pageable pageable);

    long countAllByBuilding(InventoryEntity building);

    boolean existsFloorByBuilding(InventoryEntity building);
}
