package ru.gctc.inventory.server.vaadin.providers;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.ResolvableType;
import org.springframework.stereotype.Component;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.services.InventoryService;

import java.util.List;

@Component
public class InventoryEntityManagerFactory {
    @Autowired
    private List<InventoryService<? extends InventoryEntity>> services;

    public <IE extends InventoryEntity> InventoryEntityManager<IE> build(IE entity) {
        ResolvableType targetServiceType = ResolvableType.forClassWithGenerics(
                InventoryService.class,
                entity.getClass());
        /* manual type checking */
        InventoryService<IE> service = (InventoryService<IE>)
                services
                        .stream()
                        .filter(targetServiceType::isInstance)
                        .findFirst().orElseThrow();
        return new InventoryEntityManager<>(entity, service);
    }
}
