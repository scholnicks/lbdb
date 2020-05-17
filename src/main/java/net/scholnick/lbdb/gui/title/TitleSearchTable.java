package net.scholnick.lbdb.gui.title;

import net.scholnick.lbdb.gui.SearchTableCellRenderer;

import javax.swing.*;
import javax.swing.table.TableColumnModel;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

public final class TitleSearchTable extends JTable {
    public TitleSearchTable() {
        super(new TitleSearchTableModel());
        initialize();
        defineColumnWidths();
        loadRenderers();
        addListeners();
    }

    private void initialize() {
        setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        setCellSelectionEnabled(false);
        setRowSelectionAllowed(true);
        getTableHeader().setReorderingAllowed(false);
        getTableHeader().setDefaultRenderer(new HeaderRenderer(this));
    }

    private void defineColumnWidths() {
        // 800

        TableColumnModel columnModel = getColumnModel();
        columnModel.getColumn(0).setPreferredWidth(300);
        columnModel.getColumn(1).setPreferredWidth(225);
        columnModel.getColumn(2).setPreferredWidth(100);
        columnModel.getColumn(3).setPreferredWidth(100);
        columnModel.getColumn(4).setPreferredWidth(75);
    }

    private void loadRenderers() {
        TableColumnModel columnModel = getColumnModel();

        for (int i = 0; i <= 4; i++)
            columnModel.getColumn(i).setCellRenderer(SearchTableCellRenderer.getInstance());
    }

    private void addListeners() {
        getTableHeader().addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent e) {
                int selectedColumn = getTableHeader().columnAtPoint(e.getPoint());
                ((TitleSearchTableModel) getModel()).sort(selectedColumn);
            }
        });
    }

    @Override
    public String toString() {
        return String.valueOf(getModel());
    }

}
