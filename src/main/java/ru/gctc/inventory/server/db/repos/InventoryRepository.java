package ru.gctc.inventory.server.db.repos;

import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.NoRepositoryBean;
import ru.gctc.inventory.server.db.entities.InventoryEntity;

@NoRepositoryBean
public interface InventoryRepository<IE extends InventoryEntity> extends CrudRepository<IE, Long> {


}
