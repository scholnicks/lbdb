package net.scholnick.lbdb.domain;

import lombok.*;

import java.util.Arrays;

@Getter
public enum Media implements Comparable<Media> {
    BOOK(1,"Book"),
    KINDLE(2,"Kindle"),
    NOOK(3,"Nook"),
    AUDIBLE(4,"Audible Audio Book")
    ;

    private final int    id;
    private final String description;

    Media(int id, String description) {
        this.id = id;
        this.description = description;
    }

    @Override
    public String toString() {
        return description;
    }

    public static Media from(Integer id) {
        if (id == null) return null;
        return Arrays.stream(values()).filter(m -> m.id == id).findFirst().orElse(null);
    }
}
