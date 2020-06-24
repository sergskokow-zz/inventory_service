package ru.gctc.inventory.server.db.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.Building;
import ru.gctc.inventory.server.db.entities.Floor;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.repos.FloorRepository;

import java.util.List;

@Service
public class FloorService extends InventoryEntityService<Floor, FloorRepository> {

    public FloorService(FloorRepository repository) {
        super(repository);
    }

    @Override
    public List<Floor> getChildren(InventoryEntity parent, int offset, int limit) {
        return repository.findAllByBuilding(parent, PageRequest.of(offset, limit)).getContent();
    }

    @Override
    public long getChildCount(InventoryEntity parent) {
        return repository.countAllByBuilding(parent);
    }

    @Override
    public boolean hasChildren(InventoryEntity parent) {
        return repository.existsFloorByBuilding(parent);
    }

    @Override
    public Building getParent(Floor entity) {
        return entity.getBuilding();
    }
}
