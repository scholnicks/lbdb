package net.scholnick.lbdb.gui;

import lombok.*;

import javax.swing.JLabel;

@ToString
public class DataLabel<T extends Identifiable> extends JLabel {
    private T data;

    public static <T extends Identifiable> DataLabel<T> of(T data) {
        DataLabel<T> label = new DataLabel<>();
        label.setData(data);
        return label;
    }

    public T getData() {
        return data;
    }

    public void setData(T data) {
        this.data = data;
        setText(data.getName());
    }
}
