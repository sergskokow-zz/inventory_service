package ru.gctc.inventory.server.db.entities;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.ArrayList;
import java.util.List;

@Entity
@Table(name = "floors")
@Getter
@Setter
@NoArgsConstructor
public class Floor extends InventoryEntity {
    @ManyToOne
    @JoinColumn(name = "building_id", nullable = false)
    private Building building;

    @OneToMany(mappedBy = "floor", cascade = CascadeType.ALL)
    private List<Room> rooms = new ArrayList<>();

    @Column(nullable = false)
    private Integer number;

    public Floor(int number) {
        this.number = number;
    }

    @Override
    public String toString() {
        return "Этаж №"+number.toString();
    }
}
