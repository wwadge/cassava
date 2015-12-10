package cassava.csv.core.objects;

import cassava.csv.core.CsvField;
import cassava.csv.core.CsvType;
import lombok.Data;

import java.util.Map;

/**
 * @author Andrew Vella
 * @since 09/12/15.
 */
@CsvType
@Data
public class TestClassWithMap {
    @CsvField(headerName = "age")
    private Integer age;

    @CsvField
    private Map testMap;

}
