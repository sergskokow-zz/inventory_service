package ru.gctc.inventory.server.db.repos;

import org.springframework.stereotype.Repository;
import ru.gctc.inventory.server.db.entities.Container;

@Repository
public interface ContainerRepository extends InventoryRepository<Container> {

}
