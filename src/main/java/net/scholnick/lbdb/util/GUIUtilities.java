package net.scholnick.lbdb.util;

import net.scholnick.lbdb.BooksDB;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

public class GUIUtilities {
    private GUIUtilities() {}

    public static Set<Component> components(Container root) {
        return components(root,new HashSet<>());
    }

    private static Set<Component> components(Container root, Set<Component> found) {
        if (root == null || root.getComponents() == null) {
            return found;
        }

        for (Component c: root.getComponents()) {
            found.add(c);
            if (c instanceof Container n) components(n,found);
        }

        return found;
    }

    public static JPanel panel(JComponent c) {
        JPanel p = new JPanel(new FlowLayout());
        p.add(c);
        return p;
    }

    public static JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setForeground(BooksDB.FOREGROUND_COLOR);
        b.setBackground(BooksDB.BACKGROUND_COLOR);
        b.setOpaque(true);
        b.setBorderPainted(false);
        return b;
    }

    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            throw new ApplicationException("Unable to set look and feel",e);
        }
    }

    public static void setSizes(JComponent component, Dimension dimension) {
        component.setSize(dimension);
        component.setPreferredSize(dimension);
        component.setMinimumSize(dimension);
        component.setMaximumSize(dimension);
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
