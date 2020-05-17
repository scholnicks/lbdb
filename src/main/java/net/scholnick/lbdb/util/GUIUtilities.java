package net.scholnick.lbdb.util;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.DefaultTableCellRenderer;
import javax.swing.table.TableModel;
import java.awt.*;
import java.util.Objects;

public class GUIUtilities {
    private final static Logger log = LoggerFactory.getLogger(GUIUtilities.class);

    private GUIUtilities() {
    }

    public static GridBagConstraints getDefaultGridBagConstraints() {
        GridBagConstraints gbc = new GridBagConstraints();
        gbc.gridx = gbc.gridy = 0;
        gbc.gridheight = 1;
        gbc.gridwidth = 1;
        gbc.weightx = 1.00;
        gbc.weighty = .50;
        gbc.anchor = GridBagConstraints.WEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = EMPTY_INSETS;

        return gbc;
    }

    public static void center(Window w) {
        Dimension d = w.getSize();
        Dimension screenSize = w.getToolkit().getScreenSize();
        w.setLocation((screenSize.width - d.width) / 2, (screenSize.height - d.height) / 2);
    }

    public static void setCellsAlignment(JTable table, int alignment) {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(alignment);

        TableModel tableModel = table.getModel();
        for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++) {
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(rightRenderer);
        }
    }

    public static void showMessageDialog(String message) {
        showMessageDialog(message, null);
    }

    public static void showMessageDialog(String message, String title) {
        ImageIcon BOOKCASE_ICON = new ImageIcon(Objects.requireNonNull(GUIUtilities.class.getClassLoader().getResource("images/bookcase.gif")));
        JOptionPane.showMessageDialog(null, message, title, JOptionPane.PLAIN_MESSAGE, BOOKCASE_ICON);
    }

//	private static final ImageIcon BOOKCASE_ICON = new ImageIcon(Objects.requireNonNull(GUIUtilities.class.getClassLoader().getResource("images/bookcase.gif")));

    public static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);

    private static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);
}
