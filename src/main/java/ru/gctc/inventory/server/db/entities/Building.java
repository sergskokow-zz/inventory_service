package ru.gctc.inventory.server.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "buildings")
@Getter
@Setter
@NoArgsConstructor
public class Building extends InventoryEntity {
    @OneToMany(mappedBy = "building", cascade = CascadeType.ALL)
    private List<Floor> floors = new ArrayList<>();

    @Column(nullable = false, unique = true)
    @NotNull
    private String name;

    public Building(String name) {
        this.name = name;
    }
}
