package net.scholnick.lbdb.gui.title;

import net.scholnick.lbdb.domain.Author;

import javax.swing.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

@Deprecated
public final class AuthorPanel extends JPanel {
    private JTextField field;
    private Long authorId;
    private JCheckBox editor;
//	private JList      autoSelectList;

    public Author get() {
        Author a = new Author();
        a.setId(authorId);
        a.setEditor(getEditor().isSelected());

        return a;
    }

//    public void set(Author a) {
//        authorId = a.getId();
//        getField().setText(a.getName());
//        getEditor().setSelected(a.isEditor());
//    }

    private JTextField getField() {
        if (field == null) {
            field = new JTextField();

            field.addKeyListener(new KeyAdapter() {
                @Override public void keyPressed(KeyEvent e) {}
            });
        }
        return field;
    }

//    public Long getAuthorId() {
//        return authorId;
//    }

//    public void setAuthorId(Long authorId) {
//        this.authorId = authorId;
//    }

    private JCheckBox getEditor() {
        if (editor == null) {
            editor = new JCheckBox();
        }

        return editor;
    }

	/*
	public JList getAutoSelectList() {
		if (autoSelectList == null) {
			autoSelectList = new JList();
		}
		
		return autoSelectList;
	}
	*/
}
