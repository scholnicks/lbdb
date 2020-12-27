package net.scholnick.lbdb.gui;

import javax.swing.*;
import java.awt.event.*;
import java.util.*;
import java.util.function.*;

import static java.util.stream.Collectors.toList;

public class SelectTextField<T> extends JTextField  {
    private final Supplier<List<T>> supplier;
    private Consumer<T>             consumer;
    private int                     keysTrigger;

    public static <T> SelectTextField<T> of(int columns, int keysTrigger, Supplier<List<T>> supplier, Consumer<T> consumer) {
        SelectTextField<T> field = new SelectTextField<>(columns,supplier);
        field.setConsumer(consumer);
        field.setKeysTrigger(keysTrigger);
        return field;
    }

    public SelectTextField(int columns, Supplier<List<T>> supplier) {
        super(columns);
        this.supplier    = Objects.requireNonNull(supplier);
        this.consumer    = (e) -> setText(e.toString());
        this.keysTrigger = 3;
//        addPopupListener();
    }

    private void addPopupListener() {
        final JTextField owner = this;
        addKeyListener(new KeyAdapter() {
            @Override public void keyReleased(KeyEvent e) {
                if (getText().length() >= getKeysTrigger()) {
                    JPopupMenu popup = new JPopupMenu();
                    for (T d: supplier.get().stream().filter(Objects::nonNull).limit(20).collect(toList())) {
                        JMenuItem item = new JMenuItem(d.toString());
                        item.addActionListener((e1) -> consumer.accept(d));
                        popup.add(item);
                    }
                    popup.pack();
                    popup.show(owner,0,owner.getHeight());
                    //popup.requestFocusInWindow();
                }
            }
        });
    }

    public final int getKeysTrigger() {
        return keysTrigger;
    }

    public final void setKeysTrigger(int keysTrigger) {
        if (keysTrigger < 1) throw new IllegalArgumentException("keysTrigger must be greater than 0");
        this.keysTrigger = keysTrigger;
    }

    public final Supplier<List<T>> getSupplier() {
        return supplier;
    }

    public final Consumer<T> getConsumer() {
        return consumer;
    }

    public final void setConsumer(Consumer<T> consumer) {
        this.consumer = Objects.requireNonNull(consumer);
    }
}
