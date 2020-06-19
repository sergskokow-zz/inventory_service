package ru.gctc.inventory.server;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.transaction.annotation.Transactional;
import ru.gctc.inventory.server.db.entities.*;
import ru.gctc.inventory.server.db.services.BuildingService;
import ru.gctc.inventory.server.db.services.ItemService;
import ru.gctc.inventory.server.db.services.PlaceService;
import ru.gctc.inventory.server.vaadin.providers.InventoryEntityManager;
import ru.gctc.inventory.server.vaadin.providers.InventoryEntityManagerFactory;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@SpringBootTest
//@Disabled
public class SimpleTest {

    @Autowired
    BuildingService buildingService;
    @Autowired
    ItemService itemService;

    static final Logger log = LoggerFactory.getLogger(SimpleTest.class);

    @Test
    public void serviceMustBeAutowired() {
        assertNotNull(buildingService);
    }

    @Test
    public void serviceCanListObjects() {
        assertNotNull(buildingService.getAll());
    }

    @Test
    public void objectsHaveNames() {
        assertEquals("Здание №1", buildingService.getById(1L).get().getName());
        assertEquals("Здание №2", buildingService.getById(2L).get().getName());
    }

    @Test
    @Transactional
    public void buildingHaveFloors() {
        List<? extends InventoryEntity> floors = buildingService.getChildren(1L);
        assertEquals(2, floors.size());
    }

    public void logSpecificItemPath(Item item) {
        log.info("============================");
        log.info(item.toString());

        InventoryEntity itemPlace = itemService.getParent(item).orElse(null);
        assertNotNull(itemPlace);
        assertTrue(itemPlace instanceof Place || itemPlace instanceof Room);
        Room room;
        if(itemPlace instanceof Place) {
            Place place = (Place) itemPlace;
            log.info(place.toString());

            Container container = place.getContainer();
            assertNotNull(container);
            log.info(container.toString());

            room = container.getRoom();
            assertNotNull(room);
        } else
            room = (Room) itemPlace;

        log.info(room.toString());

        Floor floor = room.getFloor();
        assertNotNull(floor);
        log.info(floor.toString());

        Building building = floor.getBuilding();
        assertNotNull(building);
        log.info(building.toString());
    }

    @ParameterizedTest
    @ValueSource(longs = {1L})
    public void logSpecificItemPathTest(long id) {
        Item item = itemService.getById(id).orElse(null);
        assertNotNull(item);
        logSpecificItemPath(item);
    }

    @Test
    public void logAllItemsWithPaths() {
        List<Item> items = itemService.getAll();
        for(Item item : items)
            logSpecificItemPath(item);
    }

    @Autowired
    InventoryEntityManagerFactory factory;
    @Test
    public void automaticServiceInjection() {
        InventoryEntityManager<? extends InventoryEntity> manager =
                factory.build(new Building());
        assertNotNull(manager.getInventoryService());
    }

    @Autowired
    PlaceService placeService;
    @Test
    public void checkContainer() {
        List<Item> items = placeService.getAllItems(1,0,1);
        assertNotNull(items);
    }

    @Test
    public void findEngines() {
        List<Item> engines = itemService.findByName("двигатель",0,10);
        assertEquals(engines.size(),2);
    }
}