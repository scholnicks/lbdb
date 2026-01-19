package net.scholnick.lbdb.gui.author;

import net.scholnick.lbdb.domain.Author;
import net.scholnick.lbdb.gui.*;
import net.scholnick.lbdb.service.AuthorService;
import net.scholnick.lbdb.util.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import javax.swing.*;
import java.awt.*;

import static javax.swing.JOptionPane.*;
import static net.scholnick.lbdb.util.GUIUtilities.showMessageDialog;

/**
 * AuthorMaintenance is a GUI component for adding, editing, and deleting Author records.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
@Component
public class AuthorMaintenance extends AbstractUpdateMaintenance {
    private JTextField nameField;
    private JButton deleteButton;

    private Author author;

    private final AuthorService authorService;

    @Autowired
    public AuthorMaintenance(AuthorService authorService) {
        super();
        this.authorService = authorService;
        buildGUI();
    }

    @Override
    protected void resetFocus() {
        getNameField().requestFocus();
    }

    @Override
    protected void clear() {
        getNameField().setText("");
        author = null;
    }

    /** Create and return the Delete button. */
    private JButton getDeleteButton() {
        if (deleteButton == null) {
            deleteButton = GUIUtilities.createButton("Delete");
            deleteButton.addActionListener(e -> removeAuthor());
        }
        return deleteButton;
    }

    /** Remove the current Author after confirming with the user. */
    private void removeAuthor() {
        if (showConfirmDialog(this, "Delete Author?") == YES_OPTION) {
            authorService.delete(author);
            showMessageDialog(author + " deleted");
            clear();
        }
    }

    @Override
    protected final JPanel getButtonPanel() {
        JPanel p = new JPanel();
        p.add(getSaveButton());
        p.add(getDeleteButton());
        p.add(getClearButton());

        return p;
    }

    @Override
    protected final JPanel getInputPanel() {
        JPanel p = new JPanel(new GridBagLayout());
        GridBagConstraints gbc = new GridBagConstraints();

        gbc.gridx = gbc.gridy = 0;
        gbc.anchor = GridBagConstraints.NORTHWEST;
        gbc.fill = GridBagConstraints.NONE;
        gbc.insets = new Insets(5, 5, 0, 0);

        p.add(LabelFactory.createLabel("Name"), gbc);
        gbc.gridx++;
        p.add(getNameField(), gbc);

        return p;
    }

    /** Get the Name text field, creating it if necessary. */
    private JTextField getNameField() {
        if (nameField == null) nameField = new TrimmedTextField(30, 255);
        return nameField;
    }

    /** Set the Author to be edited in this maintenance panel. */
    public void setAuthor(Author a) {
        author = a;
        if (author != null) loadData();
    }

    /** Load the Author data into the input fields. */
    private void loadData() {
        getNameField().setText(author.getName());
        reload();
    }

    @Override
    protected final void ok() {
        author.setName(getNameField().getText());
        authorService.save(author);
        sendMessage(author.getName() + " has been saved");
    }
}
