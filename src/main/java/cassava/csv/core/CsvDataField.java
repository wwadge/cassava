package cassava.csv.core;

import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Created by andrew on 03/11/15.
 */
@Data
@AllArgsConstructor
public class CsvDataField {
    private String headerName;
    private String fieldValue;
    private int fieldPosition;
}
