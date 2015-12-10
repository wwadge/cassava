package cassava.csv.core.objects;

import cassava.csv.core.CsvField;
import cassava.csv.core.CsvType;

/**
 * @author Andrew Vella
 * @since 18/11/15.
 */
@CsvType
public class TestClassBWithFlag {
    @CsvField(headerName = "surname")
    private String surname;

    @CsvField(headerName = "age")
    private Integer age;
}
