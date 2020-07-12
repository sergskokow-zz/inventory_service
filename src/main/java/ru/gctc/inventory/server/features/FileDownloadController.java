package ru.gctc.inventory.server.features;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.ContentDisposition;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import ru.gctc.inventory.server.db.entities.InventoryEntity;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.db.services.*;
import ru.gctc.inventory.server.vaadin.utils.InventoryEntityNames;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;

@Controller
public class FileDownloadController {
    private final BuildingService buildingService;
    private final FloorService floorService;
    private final RoomService roomService;
    private final ContainerService containerService;
    private final PlaceService placeService;
    private final ItemService itemService;
    @Autowired
    public FileDownloadController(BuildingService buildingService,
                                  FloorService floorService,
                                  RoomService roomService,
                                  ContainerService containerService,
                                  PlaceService placeService,
                                  ItemService itemService) {
        this.buildingService = buildingService;
        this.floorService = floorService;
        this.roomService = roomService;
        this.containerService = containerService;
        this.placeService = placeService;
        this.itemService = itemService;
    }

    private static final String DOCX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.wordprocessingml.document";
    private static final String XLSX_MIME_TYPE = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";

    public enum ReportType { docx, xlsx }

    @RequestMapping(value = "/download/report", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadReport(@RequestParam ReportType type,
                                                              @RequestParam List<Long> itemsId) {
        List<Item> items = (List<Item>) itemService.getAllByIds(itemsId);
        if(items.isEmpty())
            return ResponseEntity.notFound().build();
        GeneratedFile generatedFile;
        String mimeType;
        if (type == ReportType.docx) {
            generatedFile = Reports.getDocxReport("Отчёт", items);
            mimeType = DOCX_MIME_TYPE;
        } else {
            generatedFile = Reports.getXlsxReport("Отчёт", items);
            mimeType = XLSX_MIME_TYPE;
        }
        return response(generatedFile.getFileName(), mimeType, generatedFile.getInputStream());
    }

    @RequestMapping(value = "/download/report/by_parent", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadReport(@RequestParam ReportType type,
                                                              @RequestParam int parentType,
                                                              @RequestParam long parentId) {
        InventoryEntity parent;
        Optional<? extends InventoryEntity> searchResult;
        searchResult = getById(parentType, parentId);

        if(searchResult.isPresent())
            parent = searchResult.get();
        else
            return ResponseEntity.notFound().build();

        GeneratedFile generatedFile;
        String mimeType;
        if(type == ReportType.docx) {
            generatedFile = Reports.getDocxReport(InventoryEntityNames.get(parent), itemService.getAllChildren(parent));
            mimeType = DOCX_MIME_TYPE;
        } else {
            generatedFile = Reports.getXlsxReport(InventoryEntityNames.get(parent), itemService.getAllChildren(parent));
            mimeType = XLSX_MIME_TYPE;
        }
        return response(generatedFile.getFileName(), mimeType, generatedFile.getInputStream());
    }

    @RequestMapping(value = "/download/qr", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadQrCodes(@RequestParam List<Long> itemsId) {
        List<Item> items = (List<Item>) itemService.getAllByIds(itemsId);
        if(items.isEmpty())
            return ResponseEntity.notFound().build();
        GeneratedFile qrCodes = Reports.getQrCodes(items);
        return response(qrCodes.getFileName(), DOCX_MIME_TYPE, qrCodes.getInputStream());
    }

    @RequestMapping(value = "/download/qr/by_parent", method = RequestMethod.GET)
    public ResponseEntity<InputStreamResource> downloadQrCodes(@RequestParam int parentType,
                                                               @RequestParam long parentId) {
        InventoryEntity parent;
        Optional<? extends InventoryEntity> searchResult;
        searchResult = getById(parentType, parentId);

        if(searchResult.isPresent())
            parent = searchResult.get();
        else
            return ResponseEntity.notFound().build();

        GeneratedFile qrCodes = Reports.getQrCodes(itemService.getAllChildren(parent));
        return response(qrCodes.getFileName(), DOCX_MIME_TYPE, qrCodes.getInputStream());
    }

    private Optional<? extends InventoryEntity> getById(int type, long id) {
        switch (type) {
            case 0:
                return buildingService.getById(id);
            case 1:
                return floorService.getById(id);
            case 2:
                return roomService.getById(id);
            case 3:
                return containerService.getById(id);
            case 4:
                return placeService.getById(id);
            default:
                return Optional.empty();
        }
    }

    private ResponseEntity<InputStreamResource> response(String fileName, String mime, InputStream stream) {
        InputStreamResource resource = new InputStreamResource(stream);
        HttpHeaders headers = new HttpHeaders();
        headers.setContentDisposition(ContentDisposition.builder("attachment")
                .filename(fileName, StandardCharsets.UTF_8)
                .build());
        return ResponseEntity.ok()
                .headers(headers)
                .contentType(MediaType.parseMediaType(mime))
                .body(resource);
    }
}
