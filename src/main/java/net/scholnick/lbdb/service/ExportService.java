package net.scholnick.lbdb.service;

import lombok.extern.slf4j.Slf4j;
import net.scholnick.lbdb.repository.*;
import net.scholnick.lbdb.util.NullSafe;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.stream.IntStream;

import static java.util.Comparator.comparing;

@Slf4j
@Service
public class ExportService {
    private final BookRepository   bookRepository;
    private final AuthorRepository authorRepository;

    private static final List<String> HEADER_ROW = List.of("Title","Media","Authors");

    @Autowired
    public ExportService(BookRepository bookRepository, AuthorRepository authorRepository) {
        this.bookRepository   = bookRepository;
        this.authorRepository = authorRepository;
    }

    public String export() {
        try {
            exportAuthors();
            exportTitles();
            return "Created files in " + getOutputDirectory();
        }
        catch (IOException e) {
            log.error("Unable to export",e);
            return "Error: " + e.getLocalizedMessage();
        }
    }

    private String getOutputDirectory() {
        return "production".equalsIgnoreCase(System.getProperty("lbdb.environment","dev")) ?
            "/Users/steve/data/" :
            "/Users/steve/Desktop/"
        ;
    }

    private void exportTitles() throws IOException  {
        try (var workbook = new XSSFWorkbook(); var fous = new FileOutputStream(getOutputDirectory() + "titles.xlsx")) {
            XSSFSheet sheet = workbook.createSheet("Titles");

            CellStyle bold = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            bold.setFont(font);

            Row titleRow = sheet.createRow(0);
            for (int c=0; c < HEADER_ROW.size(); c++) {
                Cell cell = titleRow.createCell(c);
                cell.setCellStyle(bold);
                cell.setCellValue(HEADER_ROW.get(c));
            }

            int r = 1;
            for (TitleReportData data : bookRepository.titleData().stream().sorted(comparing(TitleReportData::slug)).toList()) {
                 Row row = sheet.createRow(r);
                 Cell cell = row.createCell(0);
                 cell.setCellValue(data.title());

                 cell = row.createCell(1);
                 cell.setCellValue(data.media());

                 cell = row.createCell(2);
                 cell.setCellValue(data.authors());

                 r++;
            }

            IntStream.of(0,1,2).forEach(sheet::autoSizeColumn);

            sheet.createFreezePane(0,1);

            workbook.write(fous);
        }
    }

    private void exportAuthors() throws IOException  {
        try (var workbook = new XSSFWorkbook(); var fous = new FileOutputStream(getOutputDirectory() + "authors.xlsx")) {
            XSSFSheet sheet = workbook.createSheet("Authors");

            CellStyle bold = workbook.createCellStyle();
            Font font = workbook.createFont();
            font.setBold(true);
            bold.setFont(font);

            Row titleRow = sheet.createRow(0);
            Cell titleCell = titleRow.createCell(0);
            titleCell.setCellStyle(bold);
            titleCell.setCellValue("Name");

            int r = 1;
            for (String data : authorRepository.allNames()) {
                Row row = sheet.createRow(r);
                Cell cell = row.createCell(0);
                cell.setCellValue(data);
                r++;
            }

            sheet.autoSizeColumn(0);

            sheet.createFreezePane(0,1);

            workbook.write(fous);
        }
    }

    public record TitleReportData(String title, String media, String authors) {
        String slug() {
            if (NullSafe.isEmpty(title)) return "";
            if (title.startsWith("The ")) return title.substring("The ".length()).toLowerCase().strip();
            if (title.startsWith("A ")) return title.substring("A ".length()).toLowerCase().strip();
            if (title.startsWith("An ")) return title.substring("An ".length()).toLowerCase().strip();
            return title.toLowerCase().strip();
        }
    }
}
