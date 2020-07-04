package net.scholnick.lbdb.coverphoto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@JsonIgnoreProperties(ignoreUnknown=true)
public final class BookResults {
    private List<BookData> items;
}
