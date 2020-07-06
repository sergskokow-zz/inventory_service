package ru.gctc.inventory.server.features;

import lombok.Getter;
import lombok.Setter;
import lombok.Value;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.apache.poi.xwpf.usermodel.*;
import ru.gctc.inventory.server.db.entities.Item;
import ru.gctc.inventory.server.vaadin.utils.InventoryEntityNames;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.time.ZonedDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

public class Reports {
    private static String safeFileName(String rawFileName) {
        return rawFileName.replaceAll("[\\\\/:*?\"<>|]", "_");
    }
    private static final DateTimeFormatter dateTimeFormatter = DateTimeFormatter.ofPattern("d MMMM y H:mm:ss z");
    @Getter @Setter
    private static String fontFamily = "Times New Roman";

    public static GeneratedFile getDocxReport(String title, List<Item> items) {
        /* .docx document */
        XWPFDocument document = new XWPFDocument();
        /* title */
        XWPFParagraph titleParagraph = document.createParagraph();
        titleParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun titleParagraphRun = titleParagraph.createRun();
        titleParagraphRun.setText(title);
        titleParagraphRun.setFontSize(24);
        titleParagraphRun.setFontFamily(fontFamily);
        /* subtitle - current date and time */
        XWPFParagraph subtitleParagraph = document.createParagraph();
        subtitleParagraph.setAlignment(ParagraphAlignment.CENTER);
        XWPFRun subtitleParagraphRun = subtitleParagraph.createRun();
        String currentDateTime = ZonedDateTime.now().format(dateTimeFormatter);
        subtitleParagraphRun.setText(currentDateTime);
        subtitleParagraphRun.setFontSize(14);
        subtitleParagraphRun.setFontFamily(fontFamily);
        /* table */
        XWPFTable table = document.createTable(1,4);// number|name&description|numbers|dates|photo
        /* header */
        XWPFTableRow tableHeader = table.getRow(0);
        List<String> headers = List.of("№ п/п", "Наименование", "Порядковые номера", "Даты");
        Iterator<String> header = headers.iterator();
        tableHeader.getTableCells().forEach(cell -> {
            XWPFParagraph paragraph = cell.getParagraphArray(0);
            paragraph.setAlignment(ParagraphAlignment.CENTER);
            XWPFRun run = paragraph.createRun();
            run.setText(header.next());
            run.setFontSize(12);
            run.setFontFamily(fontFamily);
            run.setBold(true);
        });
        /* all items */
        int n = 1;
        for(Item item : items) {
            XWPFTableRow currentRow = table.createRow();
            List<XWPFTableCell> currentCells = currentRow.getTableCells();
            fillCells(Map.of(
                    currentCells.get(0), List.of(new NamedField(n++)),
                    currentCells.get(1), List.of(
                            new NamedField(item.getName()),
                            new NamedField(item.getDescription()),
                            new NamedField(InventoryEntityNames.itemStatus.get(item.getStatus())),
                            new NamedField("Количество", item.getCount()),
                            new NamedField("Стоимость", item.getCost())
                    ),
                    currentCells.get(2), List.of(
                            new NamedField("Инвентарный номер", item.getNumber()),
                            new NamedField("Номер накладной получения", item.getWaybill()),
                            new NamedField("Заводской номер", item.getFactory())
                    ),
                    currentCells.get(3), List.of(
                            new NamedField("Добавлено", item.getInventory()),
                            new NamedField("Дата получения", item.getIncoming()),
                            new NamedField("Ввод в эксплуатацию", item.getCommissioning()),
                            new NamedField("Дата списания", item.getWriteoff()),
                            new NamedField("Дата планового списания", item.getSheduled_writeoff())
                    )
            ));
        }
        /* total */
        XWPFRun totalParagraphRun = document.createParagraph().createRun();
        totalParagraphRun.setText("Всего: "+items.size());
        totalParagraphRun.setFontSize(14);
        totalParagraphRun.setFontFamily(fontFamily);
        /* save docx */
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            document.write(outputStream);
            document.close();
            byte[] docxData = outputStream.toByteArray();
            outputStream.close();
            return new GeneratedFile(safeFileName(String.format("%s - %s.docx", title, currentDateTime)),
                    new ByteArrayInputStream(docxData));
        } catch (Exception e) { return null; }
    }

    @Value
    private static class NamedField {
        String name; Object value;
        public NamedField(Object value) {
            this.name = null;
            this.value = value;
        }
        public NamedField(String name, Object value) {
            this.name = name;
            this.value = value;
        }
        public boolean isNotEmpty() {
            return value!=null;
        }
        @Override
        public String toString() {
            if(name==null && isNotEmpty())
                return value.toString();
            else if(name!=null && isNotEmpty())
                return name+": "+value;
            else return null;
        }
    }

    private static void fillCells(Map<XWPFTableCell, List<NamedField>> cellsData) {
        for(var cellData : cellsData.entrySet()){
            XWPFTableCell cell = cellData.getKey();
            cellData.getValue()
                    .stream()
                    .filter(NamedField::isNotEmpty)
                    .forEach(namedField -> {
                        XWPFRun run = cell.addParagraph().createRun();
                        run.setText(namedField.toString());
                        run.setFontFamily(fontFamily);
                        run.setFontSize(12);
                    });
        }
    }

    public static GeneratedFile getXlsxReport(String title, List<Item> items) {
        /* book */
        Workbook book = new XSSFWorkbook();
        Sheet sheet = book.createSheet(safeFileName(title));

        ZonedDateTime now = ZonedDateTime.now();
        String currentDateTime = now.format(dateTimeFormatter);

        /* cell types */
        CellStyle dateStyle = book.createCellStyle();
        dateStyle.setDataFormat((short) 0xE);
        CellStyle dateTimeStyle = book.createCellStyle();
        dateTimeStyle.setDataFormat((short) 0x16);
        CellStyle moneyStyle = book.createCellStyle();
        moneyStyle.setDataFormat((short) 7);

        /* title */
        Row titleRow = sheet.createRow(0);
        titleRow.createCell(1).setCellValue(title);
        Cell reportGenerationTimeCell = titleRow.createCell(2);
        reportGenerationTimeCell.setCellStyle(dateTimeStyle);
        reportGenerationTimeCell.setCellValue(now.toLocalDateTime());

        /* headers */
        String[] headersText = {
                "№ п/п", "Наименование", "Описание", "Количество", "Стоимость", "Статус",
                "Инвентарный номер", "Номер накладной получения", "Заводской номер",
                "Добавлено", "Дата получения", "Ввод в эксплуатацию", "Дата списания", "Дата планового списания"
        };
        Row headerRow = sheet.createRow(1);
        for(int i=0; i<headersText.length; i++)
            headerRow.createCell(i).setCellValue(headersText[i]);

        /* columns width */
        int[] columnWidthInChars = {
                6, 20, 20, 11, 12, 15,
                20, 20, 20,
                11, 15, 19, 13, 23
        };
        for(int i=0; i<columnWidthInChars.length; i++)
            sheet.setColumnWidth(i, 256*columnWidthInChars[i]);

        /* all items */
        int row = 2;
        for(Item item : items) {
            Row currentRow = sheet.createRow(row);
            currentRow.createCell(0).setCellValue(row - 1);
            currentRow.createCell(1).setCellValue(item.getName());
            currentRow.createCell(2).setCellValue(item.getDescription());
            currentRow.createCell(3).setCellValue(item.getCount());
            Cell costCell = currentRow.createCell(4);
            costCell.setCellStyle(moneyStyle);
            if(item.getCost()!=null)
                costCell.setCellValue(item.getCost().doubleValue());
            currentRow.createCell(5).setCellValue(InventoryEntityNames.itemStatus.get(item.getStatus()));
            currentRow.createCell(6).setCellValue(item.getNumber());
            currentRow.createCell(7).setCellValue(item.getWaybill());
            currentRow.createCell(8).setCellValue(item.getFactory());
            Cell inventoryDateCell = currentRow.createCell(9);
            inventoryDateCell.setCellStyle(dateStyle);
            inventoryDateCell.setCellValue(item.getInventory());
            Cell incomingDateCell = currentRow.createCell(10);
            incomingDateCell.setCellStyle(dateStyle);
            incomingDateCell.setCellValue(item.getIncoming());
            Cell commissioningDateCell = currentRow.createCell(11);
            commissioningDateCell.setCellStyle(dateStyle);
            commissioningDateCell.setCellValue(item.getCommissioning());
            Cell writeoffDateCell = currentRow.createCell(12);
            writeoffDateCell.setCellStyle(dateStyle);
            writeoffDateCell.setCellValue(item.getWriteoff());
            Cell sheduledWriteoffDateCell = currentRow.createCell(13);
            sheduledWriteoffDateCell.setCellStyle(dateStyle);
            sheduledWriteoffDateCell.setCellValue(item.getSheduled_writeoff());
            row++;
        }
        Row totalRow = sheet.createRow(row);
        totalRow.createCell(0).setCellValue("Всего:");
        totalRow.createCell(1).setCellValue(row-2);

        /* save xlsx */
        try {
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            book.write(outputStream);
            book.close();
            byte[] xlsxData = outputStream.toByteArray();
            outputStream.close();
            return new GeneratedFile(safeFileName(String.format("%s - %s.xlsx", title, currentDateTime)),
                    new ByteArrayInputStream(xlsxData));
        } catch (Exception e) { return null; }
    }
}
