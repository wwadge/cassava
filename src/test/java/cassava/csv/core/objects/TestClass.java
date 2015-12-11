package cassava.csv.core.objects;

import cassava.csv.core.CsvField;
import cassava.csv.core.CsvType;
import lombok.Data;

import java.time.LocalDate;

/**
 * Created by andrew on 02/11/15.
 */
@CsvType
@Data
public class TestClass {

    @CsvField(headerName = "name", outputPosition = 1)
    private String test;

    @CsvField(columnPosition = 1, outputPosition = 2)
    private String test2;

    @CsvField()
    private TestSubClass testSubClass;

    @CsvField(headerName = "inttest")
    private Integer testInt;

    @CsvField(headerName = "datetest")
    private LocalDate testDate;
}
