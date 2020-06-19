package ru.gctc.inventory.server.db.repos;

import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.Room;

@Repository
public interface RoomRepository extends InventoryRepository<Room> {

}
