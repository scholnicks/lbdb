package net.scholnick.lbdb.coverphoto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

/**
 * BookResults record to hold results from Google Books API
 *
 * @author Steve Scholnick <scholnicks@gmail.com>
 */
public record BookResults(@JsonProperty("items") List<BookData> items) {
    public record BookData(@JsonProperty("volumeInfo") VolumeInfo volumeInfo) {}
}
