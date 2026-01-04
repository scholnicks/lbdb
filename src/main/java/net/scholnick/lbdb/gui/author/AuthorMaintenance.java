package net.scholnick.lbdb.gui.author;

import lombok.Getter;
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

@Component
@Getter
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

    protected void resetFocus() {
        getNameField().requestFocus();
    }

    @Override
    protected void clear() {
        getNameField().setText("");
        author = null;
    }

    private JButton getDeleteButton() {
        if (deleteButton == null) {
            deleteButton = GUIUtilities.createButton("Delete");
            deleteButton.addActionListener(e -> removeAuthor());
        }
        return deleteButton;
    }

    private void removeAuthor() {
        if (showConfirmDialog(this, "Delete Author?") == YES_OPTION) {
            authorService.delete(getAuthor());
            showMessageDialog(author + " deleted");
            clear();
        }
    }

    @Override
    protected JPanel getButtonPanel() {
        JPanel p = new JPanel();
        p.add(getSaveButton());
        p.add(getDeleteButton());
        p.add(getClearButton());

        return p;
    }

    protected JPanel getInputPanel() {
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

    private JTextField getNameField() {
        if (nameField == null) nameField = new TrimmedTextField(30, 255);
        return nameField;
    }

    public void setAuthor(Author a) {
        author = a;
        if (author != null) loadData();
    }

    private void loadData() {
        getNameField().setText(author.getName());
        reload();
    }

    protected void ok() {
        author.setName(getNameField().getText());
        authorService.save(author);
        sendMessage(author.getName() + " has been saved");
    }
}
