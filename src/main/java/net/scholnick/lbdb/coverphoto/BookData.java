package net.scholnick.lbdb.coverphoto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

@Accessors(chain=true)
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public final class BookData {
    private VolumeInfo volumeInfo;
}
