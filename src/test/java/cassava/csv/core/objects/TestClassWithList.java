package cassava.csv.core.objects;

import cassava.csv.core.CsvField;
import cassava.csv.core.CsvType;
import lombok.Data;

import java.util.List;

/**
 * @author Andrew Vella
 * @since 13/11/15.
 */
@CsvType
@Data
public class TestClassWithList {

    @CsvField(headerName = "age")
    private Integer age;

    @CsvField
    private List<TestSubSubClass> subClassList;
}
