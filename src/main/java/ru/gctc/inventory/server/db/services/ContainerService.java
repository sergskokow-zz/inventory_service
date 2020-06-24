package ru.gctc.inventory.server.db.services;

import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.Container;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Room;
import ru.gctc.inventory.server.db.repos.ContainerRepository;

import java.util.List;

@Service
public class ContainerService extends InventoryEntityService<Container, ContainerRepository> {

    public ContainerService(ContainerRepository repository) {
        super(repository);
    }

    @Override
    public List<Container> getChildren(InventoryEntity parent, int offset, int limit) {
        return repository.findAllByRoom(parent, PageRequest.of(offset, limit)).getContent();
    }

    @Override
    public long getChildCount(InventoryEntity parent) {
        return repository.countAllByRoom(parent);
    }

    @Override
    public boolean hasChildren(InventoryEntity parent) {
        return repository.existsContainerByRoom(parent);
    }

    @Override
    public Room getParent(Container entity) {
        return entity.getRoom();
    }
}
