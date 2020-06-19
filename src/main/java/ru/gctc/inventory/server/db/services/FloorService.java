package ru.gctc.inventory.server.db.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import ru.gctc.inventory.server.db.entities.Floor;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.repos.FloorRepository;
import ru.gctc.inventory.server.db.repos.RoomRepository;

import java.util.List;
import java.util.Optional;

@Service
public class FloorService extends InventoryEntityService<Floor, FloorRepository> {
    private RoomRepository roomRepository;
    @Autowired
    public void setRoomRepository(RoomRepository roomRepository) {
        this.roomRepository = roomRepository;
    }

    public FloorService(FloorRepository repository) {
        super(repository);
    }

    @Override
    public List<? extends InventoryEntity> getChildren(Floor inventoryEntity, int offset, int limit) {
        return roomRepository.findAllByFloor(inventoryEntity, PageRequest.of(offset, limit)).getContent();
    }

    @Override
    public long getChildCount(Floor inventoryEntity) {
        return roomRepository.countAllByFloor(inventoryEntity);
    }

    @Override
    public boolean hasChildren(Floor inventoryEntity) {
        return roomRepository.existsRoomByFloor(inventoryEntity);
    }

    @Override
    public Optional<InventoryEntity> getParent(Floor inventoryEntity) {
        return Optional.of(inventoryEntity.getBuilding());
    }
}
