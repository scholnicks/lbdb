package net.scholnick.lbdb.gui;

import lombok.*;

import javax.swing.JLabel;

@Getter
@ToString
@Deprecated
public class DataLabel<T extends Identifiable> extends JLabel {
    private T data;

    public static <T extends Identifiable> DataLabel<T> of(T data) {
        DataLabel<T> label = new DataLabel<>();
        label.setData(data);
        return label;
    }

    public void setData(T data) {
        this.data = data;
        setText(data.getName());
    }
}
