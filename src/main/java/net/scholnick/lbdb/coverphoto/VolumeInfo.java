package net.scholnick.lbdb.coverphoto;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.*;

@Accessors(chain=true)
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public final class VolumeInfo {
    private String                  title;
    private Set<String>             authors;
    private int                     pageCount;
    private Map<String,String>      imageLinks;
    private Set<IndustryIdentifier> industryIdentifiers;
    private String                  publishedDate;

    public record IndustryIdentifier(@JsonProperty("type") String type, @JsonProperty("identifier") String identifier) {}
}
