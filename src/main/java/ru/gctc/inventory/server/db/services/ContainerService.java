package ru.gctc.inventory.server.db.services;

import org.hibernate.Hibernate;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.Container;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.repos.ContainerRepository;

import java.util.List;
import java.util.Optional;

@Service
public class ContainerService extends InventoryEntityService<Container, ContainerRepository> {

    public ContainerService(ContainerRepository repository) {
        super(repository);
    }

    @Override
    public List<? extends InventoryEntity> getChildren(Container inventoryEntity) {
        Hibernate.initialize(inventoryEntity.getPlaces());
        return inventoryEntity.getPlaces();
    }

    @Override
    public int getChildCount(Container inventoryEntity) {
        if(inventoryEntity.getPlaces()==null)
            return 0;
        return inventoryEntity.getPlaces().size();
    }

    @Override
    public boolean hasChildren(Container inventoryEntity) {
        return inventoryEntity.getPlaces()!=null && !inventoryEntity.getPlaces().isEmpty();
    }

    @Override
    public Optional<InventoryEntity> getParent(Container inventoryEntity) {
        return Optional.of(inventoryEntity.getRoom());
    }
}
