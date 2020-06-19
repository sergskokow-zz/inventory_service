package ru.gctc.inventory.server.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
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
    private Floor floor;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Container> containers = new ArrayList<>();

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL)
    private List<Item> items = new ArrayList<>();

    @Column(nullable = false)
    private Integer number; // TODO replace by name?

    private String name;

    public Room(int number) {
        this.number = number;
    }
    public Room(int number, String name) {
        this(number);
        this.name = name;
    }

    @Override
    public String toString() {
        return String.format("Кабинет №%d %s", number, name);
    }
}
