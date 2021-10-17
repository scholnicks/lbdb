package net.scholnick.lbdb.domain;

import lombok.*;

import java.util.Arrays;

@Getter
@ToString
public enum Media implements Comparable<Media> {
    BOOK(1,"Book"),
    KINDLE(2,"Kindle"),
    NOOK(3,"Nook")
    ;

    private final int    id;
    private final String description;

    Media(int id, String description) {
        this.id = id;
        this.description = description;
    }

    public static Media from(Integer id) {
        if (id == null) return null;
        return Arrays.stream(values()).filter(m -> m.id == id).findFirst().orElse(null);
    }
}
