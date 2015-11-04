package cassava.csv.core.typemappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

/**
 * Created by andrew on 02/11/15.
 */
public class LocalDateTypeMapper implements TypeMapper {

    @Override
    public Object fromString(String value) {
        return LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
    }

    @Override
    public Class getReturnType() {
        return LocalDate.class;
    }
}
