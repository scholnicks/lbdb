package net.scholnick.lbdb.service;

import net.scholnick.lbdb.dao.AuthorDAO;
import net.scholnick.lbdb.dao.BookDAO;
import net.scholnick.lbdb.domain.TitleReportData;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.CellStyle;
import org.apache.poi.ss.usermodel.Font;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.stream.IntStream;

@Service
public class ExportService {
    private final BookDAO   bookDAO;
    private final AuthorDAO authorDAO;

    private static final List<String> HEADER_ROW = List.of("Title","Media","Authors");

    @Autowired
    public ExportService(BookDAO bookDAO, AuthorDAO authorDAO) {
        this.bookDAO   = bookDAO;
        this.authorDAO = authorDAO;
    }

    public void export() {
        try {
            exportAuthors();
            exportTitles();
        }
        catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String getOutputDirectory() {
        return "production".equalsIgnoreCase(System.getProperty("lbdb.database.type","dev")) ?
            "/Users/steve/OneDrive/share/" :
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
                 cell.setCellValue(data.getTitle());

                 cell = row.createCell(1);
                 cell.setCellValue(data.getMedia());

                 cell = row.createCell(2);
                 cell.setCellValue(data.getAuthors());

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
