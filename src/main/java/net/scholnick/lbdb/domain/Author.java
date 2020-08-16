package net.scholnick.lbdb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;

import java.util.Set;

@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public final class Author implements Comparable<Author> {
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

    public String lastName() {
        int lastSpace = name.lastIndexOf(' ');
        if (lastSpace == -1) return name;

        String lastName = name.substring(lastSpace).trim();
        if (SUFFIXES.contains(lastName.toLowerCase())) {
            lastSpace = name.substring(0, lastSpace).trim().lastIndexOf(' ');
            if (lastSpace == -1) return name;
            return name.substring(0, lastSpace).trim();
        }
        else {
            return lastName;
        }
    }

    @Override
    public int compareTo(Author o) {
        return name.compareTo(o.name);
    }

    private static final Set<String> SUFFIXES = Set.of("i", "ii", "iii", "iv", "jr", "sr", "jr.", "sr.");
}
