package net.scholnick.lbdb.coverphoto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.experimental.Accessors;

import java.util.List;

@Accessors(chain=true)
@Data
@JsonIgnoreProperties(ignoreUnknown=true)
public final class BookResults {
    private List<BookData> items;
}
