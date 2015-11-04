package cassava.csv.core.objects;

import cassava.csv.core.CsvField;
import cassava.csv.core.CsvType;
import lombok.Data;

/**
 * Created by andrew on 02/11/15.
 */
@Data
@CsvType
public class TestSubClass {

    @CsvField(headerName = "surname")
    private String test3;

    @CsvField()
    private TestSubSubClass subSubClass;
}
