package cassava.csv.core.objects;

import cassava.csv.core.CsvField;
import cassava.csv.core.CsvType;
import lombok.Data;

/**
 * @author Andrew Vella
 * @since 13/11/15.
 */
@CsvType
@Data
public class TestClassSameHeader {
    @CsvField(headerName = "name")
    private String name;
    @CsvField(headerName = "surname")
    private String surname;
    @CsvField(headerName = "name")
    private String name2;
}
