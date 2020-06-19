package ru.gctc.inventory.server.db.services;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.Building;
import ru.gctc.inventory.server.db.entities.Floor;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.repos.BuildingRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BuildingService extends InventoryEntityService<Building, BuildingRepository>{

    public BuildingService(BuildingRepository repository) {
        super(repository);
    }

    @Override
    public List<? extends InventoryEntity> getChildren(Building inventoryEntity) {
        Hibernate.initialize(inventoryEntity.getFloors());
        return inventoryEntity.getFloors();
    }

    @Override
    public int getChildCount(Building inventoryEntity) {
        List<Floor> floors = inventoryEntity.getFloors();
        if(floors==null)
            return 0;
        return floors.size();
    }

    @Override
    public boolean hasChildren(Building inventoryEntity) {
        List<Floor> floors = inventoryEntity.getFloors();
        return floors!=null && !floors.isEmpty();
    }

    @Override
    public Optional<InventoryEntity> getParent(Building inventoryEntity) {
        return Optional.empty();
    }

}
