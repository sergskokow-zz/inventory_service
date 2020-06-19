package ru.gctc.inventory.server.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.Container;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.repos.ContainerRepository;
import ru.gctc.inventory.server.db.repos.PlaceRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ContainerService extends InventoryEntityService<Container, ContainerRepository> {
    private PlaceRepository placeRepository;
    @Autowired
    public void setPlaceRepository(PlaceRepository placeRepository) {
        this.placeRepository = placeRepository;
    }

    public ContainerService(ContainerRepository repository) {
        super(repository);
    }

    @Override
    public List<? extends InventoryEntity> getChildren(Container inventoryEntity, int offset, int limit) {
        return placeRepository.findAllByContainer(inventoryEntity, PageRequest.of(offset, limit)).getContent();
    }

    @Override
    public long getChildCount(Container inventoryEntity) {
        return placeRepository.countAllByContainer(inventoryEntity);
    }

    @Override
    public boolean hasChildren(Container inventoryEntity) {
        return placeRepository.existsPlaceByContainer(inventoryEntity);
    }

    @Override
    public Optional<InventoryEntity> getParent(Container inventoryEntity) {
        return Optional.of(inventoryEntity.getRoom());
    }
}
