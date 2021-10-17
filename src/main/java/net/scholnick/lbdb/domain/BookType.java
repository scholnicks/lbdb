package net.scholnick.lbdb.domain;

import lombok.*;

import java.util.Arrays;

@Getter
@ToString
public enum BookType implements Comparable<BookType> {
    FICTION(1,"Fiction"),
    NON_FICTION(2,"Non-Fiction"),
    TECHNICAL(3,"Technical")
    ;

    private final int    id;
    private final String description;

    BookType(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public static BookType from(Integer id) {
        if (id == null) return null;
        return Arrays.stream(values()).filter(m -> m.id == id).findFirst().orElse(null);
    }
}
