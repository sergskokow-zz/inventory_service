package ru.gctc.inventory.server.db.services;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.Floor;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.repos.FloorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FloorService extends InventoryEntityService<Floor, FloorRepository> {

    public FloorService(FloorRepository repository) {
        super(repository);
    }

    @Override
    public List<? extends InventoryEntity> getChildren(Floor inventoryEntity) {
        Hibernate.initialize(inventoryEntity.getRooms());
        return inventoryEntity.getRooms();
    }

    @Override
    public int getChildCount(Floor inventoryEntity) {
        if(inventoryEntity.getRooms()==null)
            return 0;
        return inventoryEntity.getRooms().size();
    }

    @Override
    public boolean hasChildren(Floor inventoryEntity) {
        return inventoryEntity.getRooms()!=null && !inventoryEntity.getRooms().isEmpty();
    }

    @Override
    public Optional<InventoryEntity> getParent(Floor inventoryEntity) {
        return Optional.of(inventoryEntity.getBuilding());
    }
}
