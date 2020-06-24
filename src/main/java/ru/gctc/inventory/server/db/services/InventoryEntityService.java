package ru.gctc.inventory.server.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.repos.InventoryRepository;
import ru.gctc.inventory.server.db.services.exceptions.EntityNotFoundException;

import java.util.List;
import java.util.Optional;

public abstract class InventoryEntityService
        <E extends InventoryEntity, R extends InventoryRepository<E>>
        implements InventoryService<E> {

    protected final R repository;

    /* IDEA say "Could not autowire. No beans of 'IR' type found" but it works */
    @SuppressWarnings("SpringJavaInjectionPointsAutowiringInspection")
    @Autowired
    public InventoryEntityService(R repository) {
        this.repository = repository;
    }

    @Override
    public long count() {
        return repository.count();
    }

    @Override
    public List<E> getAll(int offset, int limit) {
        return repository.findAll(PageRequest.of(offset, limit)).getContent();
    }

    @Override
    public E add(E inventoryEntity) {
        return repository.save(inventoryEntity);
    }

    @Override
    public E edit(E inventoryEntity) throws EntityNotFoundException {
        long id = inventoryEntity.getId();
        if(!repository.existsById(id))
            throw new EntityNotFoundException(inventoryEntity.getClass().getTypeName(), id);
        return repository.save(inventoryEntity);
    }

    @Override
    public Optional<E> getById(long inventoryEntityId) {
        return repository.findById(inventoryEntityId);
    }

    @Override
    public void delete(E inventoryEntity) {
        repository.delete(inventoryEntity);
    }

    @Override
    public void delete(long inventoryEntityId) {
        repository.deleteById(inventoryEntityId);
    }
}
