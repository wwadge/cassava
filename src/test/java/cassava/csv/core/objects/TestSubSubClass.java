package cassava.csv.core.objects;

import cassava.csv.core.CsvField;
import cassava.csv.core.CsvType;
import lombok.Data;

import java.util.List;

/**
 * Created by andrew on 02/11/15.
 */
@Data
@CsvType
public class TestSubSubClass {
    @CsvField(headerName = "age")
    private Integer age;

    @CsvField
    private List<TestClassB> classes;

}
