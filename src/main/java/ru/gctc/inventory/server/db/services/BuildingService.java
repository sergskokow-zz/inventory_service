package ru.gctc.inventory.server.db.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.Building;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.repos.BuildingRepository;

import java.util.List;

@Service
public class BuildingService extends InventoryEntityService<Building, BuildingRepository>{

    public BuildingService(BuildingRepository repository) {
        super(repository);
    }

    @Override
    public List<Building> getChildren(InventoryEntity parent, int offset, int limit) {
        return repository.findAll(PageRequest.of(offset, limit)).getContent();
    }

    @Override
    public long getChildCount(InventoryEntity parent) {
        return repository.count();
    }

    @Override
    public boolean hasChildren(InventoryEntity parent) {
        return repository.count() > 0;
    }

    @Override
    public Building getParent(Building entity) {
        return null;
    }
}
