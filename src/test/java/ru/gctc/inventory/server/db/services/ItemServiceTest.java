package ru.gctc.inventory.server.db.services;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.repos.ItemRepository;

import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SpringBootTest
class ItemServiceTest {
    @Autowired
    private ItemRepository itemRepository;
    @Autowired
    private ItemService itemService;

    @Test
    void getPath() {
        Item i = itemRepository.findById(1L).orElseThrow();
        var path = itemService.getPath(i);
        assertNotNull(path);
        assertTrue(path.size()>0);
    }
}