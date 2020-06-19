package ru.gctc.inventory.server.db.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.Building;
import ru.gctc.inventory.server.db.entities.Floor;

@Repository
public interface FloorRepository extends InventoryRepository<Floor> {
    Page<Floor> findAllByBuilding(Building building, Pageable pageable);

    long countAllByBuilding(Building building);

    boolean existsFloorByBuilding(Building building);
}
