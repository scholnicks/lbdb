package net.scholnick.lbdb.coverphoto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public class IndustryIdentifier {
    private String type;
    private String identifier;
}
