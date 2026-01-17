package net.scholnick.lbdb.coverphoto;

import com.fasterxml.jackson.annotation.*;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.Set;

@Accessors(chain=true)
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public final class VolumeInfo {
    private String                  title;
    private Set<String>             authors;
    private int                     pageCount;
    private Set<IndustryIdentifier> industryIdentifiers;
    private String                  publishedDate;
    private ImageLinks              imageLinks;

    public record IndustryIdentifier(@JsonProperty("type") String type, @JsonProperty("identifier") String identifier) {}
    public record ImageLinks(String smallThumbnail, String thumbnail) {}
}
