package ru.gctc.inventory.server.db.services.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Filters { private String name="", number="", waybill="", factory=""; }