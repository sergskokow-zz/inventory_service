package ru.gctc.inventory.server.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.Building;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.repos.BuildingRepository;
import ru.gctc.inventory.server.db.repos.FloorRepository;

import java.util.List;
import java.util.Optional;

@Service
public class BuildingService extends InventoryEntityService<Building, BuildingRepository>{
    private FloorRepository floorRepository;
    @Autowired
    public void setFloorRepository(FloorRepository floorRepository) {
        this.floorRepository = floorRepository;
    }

    public BuildingService(BuildingRepository repository) {
        super(repository);
    }

    @Override
    public List<? extends InventoryEntity> getChildren(InventoryEntity inventoryEntity, int offset, int limit) {
        return floorRepository.findAllByBuilding(inventoryEntity, PageRequest.of(offset, limit)).getContent();
    }

    @Override
    public long getChildCount(InventoryEntity inventoryEntity) {
        return floorRepository.countAllByBuilding(inventoryEntity);
    }

    @Override
    public boolean hasChildren(InventoryEntity inventoryEntity) {
        return floorRepository.existsFloorByBuilding(inventoryEntity);
    }

    @Override
    public Optional<InventoryEntity> getParent(Building inventoryEntity) {
        return Optional.empty();
    }

}
