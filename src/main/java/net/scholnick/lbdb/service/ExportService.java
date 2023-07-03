package net.scholnick.lbdb.service;

import lombok.extern.slf4j.Slf4j;
import net.scholnick.lbdb.repository.*;
import net.scholnick.lbdb.domain.TitleReportData;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.*;
import java.util.List;
import java.util.stream.IntStream;

@Slf4j
@Service
public class ExportService {
    private final BookRepository bookDAO;
    private final AuthorRepository authorDAO;

    private static final List<String> HEADER_ROW = List.of("Title","Media","Authors");

    @Autowired
    public ExportService(BookRepository bookDAO, AuthorRepository authorDAO) {
        this.bookDAO   = bookDAO;
        this.authorDAO = authorDAO;
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
            for (TitleReportData data : bookDAO.titleData()) {
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
            for (String data : authorDAO.allNames()) {
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
}
