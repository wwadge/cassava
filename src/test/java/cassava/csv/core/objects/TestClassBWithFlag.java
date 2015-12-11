package cassava.csv.core.objects;

import cassava.csv.core.CsvField;
import cassava.csv.core.CsvType;
import lombok.Data;

/**
 * @author Andrew Vella
 * @since 18/11/15.
 */
@CsvType
@Data
public class TestClassBWithFlag {
    @CsvField(headerName = "surname", outputPosition = 2)
    private String surname;

    @CsvField(headerName = "age", outputPosition = 1)
    private Integer age;
}
