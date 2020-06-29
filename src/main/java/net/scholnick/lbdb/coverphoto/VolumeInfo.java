package net.scholnick.lbdb.coverphoto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import lombok.ToString;

import java.util.Map;
import java.util.Set;

@ToString
@EqualsAndHashCode
@Getter
@Setter
@JsonIgnoreProperties(ignoreUnknown=true)
public final class VolumeInfo {
    private String title;
    private Set<String> authors;
    private int pageCount;
    private Map<String,String> imageLinks;
    private Set<IndustryIdentifier> industryIdentifiers;
}
