package net.scholnick.lbdb.gui;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.Supplier;

public class SelectTextField<T> extends JTextField  {
    private final Supplier<List<T>> supplier;
    private int         keysTrigger = 3;

    public SelectTextField(int columns, Supplier<List<T>> supplier) {
        super(columns);
        this.supplier = Objects.requireNonNull(supplier);
        addPopupListener();
    }

    private void addPopupListener() {
        final JTextField owner = this;
        addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                if (getText().length() == getKeysTrigger()) {
                    var popup = new JPopupMenu();
                    for (T name: supplier.get()) {
                        JMenuItem item = new JMenuItem(name.toString());
                        item.addActionListener((e1) -> owner.setText(name.toString()));
                        popup.add(item);
                    }
                    popup.pack();
                    popup.show(owner,0,owner.getHeight());
                    popup.requestFocusInWindow();
                }
            }
        });
    }

    public int getKeysTrigger() {
        return keysTrigger;
    }

    public void setKeysTrigger(int keysTrigger) {
        if (keysTrigger < 1) throw new IllegalArgumentException("keysTrigger must be greater than 0");
        this.keysTrigger = keysTrigger;
    }

    public Supplier<List<T>> getSupplier() {
        return supplier;
    }
}
