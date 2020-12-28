package net.scholnick.lbdb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import net.scholnick.lbdb.gui.Identifiable;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public final class Author implements Comparable<Author>, Identifiable {
    private Long id;
    private String name;
    private String webSite;
    private boolean editor;
    private String addedTimestamp;

    public static Author of(String name) {
        Author a = new Author();
        a.name = name;
        return a;
    }

    @Override
    public int compareTo(Author o) {
        return name.compareTo(o.name);
    }
}
