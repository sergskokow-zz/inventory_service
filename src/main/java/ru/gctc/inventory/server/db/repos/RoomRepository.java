package ru.gctc.inventory.server.db.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Room;

@Repository
public interface RoomRepository extends InventoryRepository<Room> {
    Page<Room> findAllByFloor(InventoryEntity floor, Pageable pageable);

    int countAllByFloor(InventoryEntity floor);

    boolean existsRoomByFloor(InventoryEntity floor);
}
