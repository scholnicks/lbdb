package net.scholnick.lbdb.util;

import net.scholnick.lbdb.BooksDB;

import javax.swing.*;
import javax.swing.border.Border;
import javax.swing.table.*;
import java.awt.*;
import java.util.*;

/**
 * GUIUtilities is a collection of utility methods for GUI programming.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
public class GUIUtilities {
    public static final Border EMPTY_BORDER = BorderFactory.createEmptyBorder(5, 5, 5, 5);
    public static final Insets EMPTY_INSETS = new Insets(0, 0, 0, 0);

    /** Returns a set of all components contained within the given root container. */
    public static Set<Component> components(Container root) {
        return components(root,new HashSet<>());
    }

    /** Recursive helper method for components. */
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

    /** Wraps the given component in a JPanel with FlowLayout. */
    public static JPanel panel(JComponent c) {
        JPanel p = new JPanel(new FlowLayout());
        p.add(c);
        return p;
    }

    /** Creates a JButton with standard BooksDB styling. */
    public static JButton createButton(String text) {
        JButton b = new JButton(text);
        b.setForeground(BooksDB.FOREGROUND_COLOR);
        b.setBackground(BooksDB.BACKGROUND_COLOR);
        b.setCursor(Cursor.getPredefinedCursor(Cursor.HAND_CURSOR));
        b.setBorder(BorderFactory.createCompoundBorder(
            BorderFactory.createLineBorder(BooksDB.BACKGROUND_COLOR),
            BorderFactory.createEmptyBorder(5,10,5,10)
        ));
        b.setOpaque(true);
        b.setBorderPainted(false);
        return b;
    }

    /** Sets the look and feel to the system look and feel. */
    public static void setLookAndFeel() {
        try {
            UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
        }
        catch (ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeelException e) {
            throw new ApplicationException("Unable to set look and feel",e);
        }
    }

    /** Sets all size properties of the given component to the given dimension. */
    public static void setSizes(JComponent component, Dimension dimension) {
        component.setSize(dimension);
        component.setPreferredSize(dimension);
        component.setMinimumSize(dimension);
        component.setMaximumSize(dimension);
    }

    /** Returns a GridBagConstraints object with default BooksDB settings. */
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

    /** Centers the given window on the screen. */
    public static void center(Window w) {
        Dimension d = w.getSize();
        Dimension screenSize = w.getToolkit().getScreenSize();
        w.setLocation((screenSize.width - d.width) / 2, (screenSize.height - d.height) / 2);
    }

    /** Sets the horizontal alignment of all cells in the given JTable. */
    public static void setCellsAlignment(JTable table, int alignment) {
        DefaultTableCellRenderer rightRenderer = new DefaultTableCellRenderer();
        rightRenderer.setHorizontalAlignment(alignment);

        TableModel tableModel = table.getModel();
        for (int columnIndex = 0; columnIndex < tableModel.getColumnCount(); columnIndex++) {
            table.getColumnModel().getColumn(columnIndex).setCellRenderer(rightRenderer);
        }
    }

    /** Shows a message dialog with the given message and title. */
    public static void showMessageDialog(Component component, String message, String title) {
        JOptionPane.showMessageDialog(
            component,
            message,
            title,
            JOptionPane.PLAIN_MESSAGE,
            new ImageIcon(Objects.requireNonNull(GUIUtilities.class.getClassLoader().getResource("images/bookcase.gif")))
        );
    }

    private GUIUtilities() {}

    /** A default list cell renderer that can be extended as needed. */
    public static final class ListCellRenderer extends DefaultListCellRenderer {
        @Override public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
            return super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);
        }
    }
}
