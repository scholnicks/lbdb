package net.scholnick.lbdb.coverphoto;

import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.List;

public record BookResults(@JsonProperty("items") List<BookData> items) {
    public record BookData(@JsonProperty("volumeInfo") VolumeInfo volumeInfo) {}
}
