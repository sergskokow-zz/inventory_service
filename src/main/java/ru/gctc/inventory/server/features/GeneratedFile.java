package ru.gctc.inventory.server.features;

import lombok.AllArgsConstructor;
import lombok.Value;

import java.io.InputStream;

@Value
@AllArgsConstructor
public class GeneratedFile {
    String fileName;
    InputStream inputStream;
}