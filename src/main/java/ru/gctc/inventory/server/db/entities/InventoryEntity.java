package ru.gctc.inventory.server.db.entities;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
@Getter
@Setter
public abstract class InventoryEntity implements Serializable {
    @Id
    @GeneratedValue
    private Long id;

    @Override
    public abstract String toString();
}
