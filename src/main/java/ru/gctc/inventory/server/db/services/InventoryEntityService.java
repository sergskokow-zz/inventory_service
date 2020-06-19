package ru.gctc.inventory.server.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.repos.InventoryRepository;
import ru.gctc.inventory.server.db.services.exceptions.EntityAlreadyExistsException;
import ru.gctc.inventory.server.db.services.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public abstract class InventoryEntityService
        <IE extends InventoryEntity, IR extends InventoryRepository<IE>>
        implements InventoryService<IE> {

    protected final IR repository;

    /* IDEA say "Could not autowire. No beans of 'IR' type found" but it works */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public InventoryEntityService(IR repository) {
        this.repository = repository;
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<IE> getAll(int offset, int limit) {
        return repository.findAll(PageRequest.of(offset, limit)).getContent();
    }

    @Override
    public IE add(IE inventoryEntity) throws EntityAlreadyExistsException {
        long id = inventoryEntity.getId();
        if(repository.existsById(id))
            throw new EntityAlreadyExistsException(inventoryEntity.getClass().getTypeName(), id, inventoryEntity.toString());
        return repository.save(inventoryEntity);
    }

    @Override
    public IE edit(IE inventoryEntity) throws EntityNotFoundException {
        long id = inventoryEntity.getId();
        if(!repository.existsById(id))
            throw new EntityNotFoundException(inventoryEntity.getClass().getTypeName(), id);
        return repository.save(inventoryEntity);
    }

    @Override
    public Optional<IE> getById(long inventoryEntityId) {
        return repository.findById(inventoryEntityId);
    }

    @Override
    public void delete(IE inventoryEntity) {
        repository.delete(inventoryEntity);
    }

    @Override
    public void delete(long inventoryEntityId) {
        repository.deleteById(inventoryEntityId);
    }
}
