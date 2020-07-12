package ru.gctc.inventory.server.db.entities;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.sql.Blob;

@Entity
@Table(name = "photos")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class Photo extends InventoryEntity {
    @OneToOne
    @JoinColumn(name = "item_id")
    private Item item;

    @Column(nullable = false)
    private String mime;

    @Lob
    @Column(nullable = false)
    private Blob data;

    public Photo(Item item) {
        this.item = item;
    }
}
