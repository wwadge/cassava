package cassava.csv.core.objects;

import cassava.csv.core.CsvField;
import cassava.csv.core.CsvType;
import lombok.Data;

/**
 * @author Andrew Vella
 * @since 11/12/15.
 */
@CsvType
@Data
public class TestWriteClassB {

    @CsvField(outputPosition = 1)
    private String name;

    @CsvField(outputPosition = 2)
    private TestClassBWithFlag testClassBWithFlag;

    @CsvField(outputPosition = 3)
    private String surname;


}
