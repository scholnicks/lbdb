package net.scholnick.lbdb.domain;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

/**
 * Author is a representation of a book author.
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
@Accessors(chain=true)
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public final class Author {
    private Long id;
    private String name;
    private boolean editor;

    public static Author of(String name) {
        return new Author().setName(name);
    }
}
