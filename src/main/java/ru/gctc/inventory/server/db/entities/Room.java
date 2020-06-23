package ru.gctc.inventory.server.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import javax.validation.constraints.NotNull;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "rooms")
@Getter
@Setter
@NoArgsConstructor
public class Room extends ContainsItems {
    @ManyToOne
    @JoinColumn(name = "floor_id", nullable = false)
    @NotNull
    private Floor floor;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Container> containers = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();

    @Column(nullable = false)
    @NotNull
    private Integer number; // TODO replace by name?

    private String name;

    public Room(Floor floor, int number) {
        this.floor = floor;
        this.number = number;
    }
    public Room(Floor floor, int number, String name) {
        this(floor, number);
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Кабинет №%d %s", number, name);
    }
}
