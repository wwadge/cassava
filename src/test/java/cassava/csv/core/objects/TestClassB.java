package cassava.csv.core.objects;

import cassava.csv.core.CsvField;
import cassava.csv.core.CsvType;

/**
 * @author Andrew Vella
 * @since 13/11/15.
 */
@CsvType
public class TestClassB {

    @CsvField(headerName = "surname")
    private String surname;
}
