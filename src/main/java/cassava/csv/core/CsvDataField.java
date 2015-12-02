package cassava.csv.core;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * @author Andrew Vella
 * @since 04/11/15.
 */
@Data
@AllArgsConstructor
public class CsvDataField {
    private String headerName;
    private String fieldValue;
    private int fieldPosition;
}
