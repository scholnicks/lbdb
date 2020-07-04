package net.scholnick.lbdb.coverphoto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Map;
import java.util.Set;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public final class VolumeInfo {
    private String title;
    private Set<String> authors;
    private int pageCount;
    private Map<String,String> imageLinks;
    private Set<IndustryIdentifier> industryIdentifiers;
}
