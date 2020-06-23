package ru.gctc.inventory.server.db.repos;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.Container;
import ru.gctc.inventory.server.db.entities.InventoryEntity;

@Repository
public interface ContainerRepository extends InventoryRepository<Container> {
    Page<Container> findAllByRoom(InventoryEntity room, Pageable pageable);

    long countAllByRoom(InventoryEntity room);

    boolean existsContainerByRoom(InventoryEntity room);
}
