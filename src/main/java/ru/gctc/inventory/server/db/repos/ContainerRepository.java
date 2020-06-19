package ru.gctc.inventory.server.db.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.Container;
import ru.gctc.inventory.server.db.entities.Room;

@Repository
public interface ContainerRepository extends InventoryRepository<Container> {
    Page<Container> findAllByRoom(Room room, Pageable pageable);

    long countAllByRoom(Room room);

    boolean existsContainerByRoom(Room room);
}
