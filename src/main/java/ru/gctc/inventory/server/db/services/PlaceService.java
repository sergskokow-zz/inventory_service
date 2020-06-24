package ru.gctc.inventory.server.db.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.Container;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Place;
import ru.gctc.inventory.server.db.repos.PlaceRepository;

import java.util.List;

@Service
public class PlaceService extends InventoryEntityService<Place, PlaceRepository> {

    public PlaceService(PlaceRepository repository) {
        super(repository);
    }

    @Override
    public List<Place> getChildren(InventoryEntity parent, int offset, int limit) {
        return repository.findAllByContainer(parent, PageRequest.of(offset, limit)).getContent();
    }

    @Override
    public long getChildCount(InventoryEntity parent) {
        return repository.countAllByContainer(parent);
    }

    @Override
    public boolean hasChildren(InventoryEntity parent) {
        return repository.existsPlaceByContainer(parent);
    }

    @Override
    public Container getParent(Place entity) {
        return entity.getContainer();
    }
}
