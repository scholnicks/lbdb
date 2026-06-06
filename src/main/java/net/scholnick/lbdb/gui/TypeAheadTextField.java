package net.scholnick.lbdb.gui;

import net.scholnick.lbdb.util.*;

import javax.swing.*;
import javax.swing.event.DocumentEvent;
import javax.swing.event.DocumentListener;
import java.awt.*;
import java.awt.event.*;
import java.util.List;

/**
 * TypeAheadTextField
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
public final class TypeAheadTextField extends JTextField {
    private final DataProvider dataProvider;
    private final JPopupMenu popup = new JPopupMenu();
    private final JList<String> suggestionList = new JList<>();

    public static TypeAheadTextField create(int columns, Dimension size, DataProvider dataProvider) {
        TypeAheadTextField t = new TypeAheadTextField(columns,dataProvider);
        GUIUtilities.setSizes(t,size);
        return t;
    }

    private TypeAheadTextField(int columns, DataProvider dataProvider) {
        super(columns);
        this.dataProvider = dataProvider;
        init();
    }

    @Override
    public String getText() {
        return NullSafe.trim(super.getText());
    }

    private void init() {
        popup.add(new JScrollPane(suggestionList));

        getDocument().addDocumentListener(new DocumentListener() {
            public void insertUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            public void removeUpdate(DocumentEvent e) {
                updateSuggestions();
            }

            public void changedUpdate(DocumentEvent e) {
                updateSuggestions();
            }
        });

        suggestionList.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                selectSuggestion();
            }
        });

        addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (!popup.isVisible()) return;

                if (e.getKeyCode() == KeyEvent.VK_DOWN) {
                    suggestionList.requestFocus();
                    suggestionList.setSelectedIndex(0);
                }
            }
        });

        suggestionList.addKeyListener(new KeyAdapter() {
            public void keyPressed(KeyEvent e) {
                if (e.getKeyCode() == KeyEvent.VK_ENTER) {
                    selectSuggestion();
                }
            }
        });
    }

    private void updateSuggestions() {
        String text = getText();

        if (text.isBlank()) {
            popup.setVisible(false);
            return;
        }

        List<String> suggestions = dataProvider.search(text);

        if (suggestions.isEmpty()) {
            popup.setVisible(false);
            return;
        }

        suggestionList.setListData(suggestions.toArray(new String[0]));
        suggestionList.setVisibleRowCount(Math.min(suggestions.size(), 5));

        popup.show(this, 0, getHeight());
        requestFocusInWindow();
    }

    private void selectSuggestion() {
        String selected = suggestionList.getSelectedValue();

        if (selected != null) {
            setText(selected);
            popup.setVisible(false);
        }
    }
}