package ru.gctc.inventory.server.db.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.Floor;
import ru.gctc.inventory.server.db.entities.Room;

@Repository
public interface RoomRepository extends InventoryRepository<Room> {
    Page<Room> findAllByFloor(Floor floor, Pageable pageable);

    long countAllByFloor(Floor floor);

    boolean existsRoomByFloor(Floor floor);
}
