package cassava.csv.core.typemappers;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author Andrew Vella
 * @since 02/11/15.
 */
public class LocalDateTypeMapper implements TypeMapper {

    @Override
    public Object fromString(String value) {
        if(Optional.ofNullable(value).isPresent() && !value.isEmpty()) {
            return LocalDate.parse(value, DateTimeFormatter.ISO_DATE);
        }
        return null;
    }

    @Override
    public Class getReturnType() {
        return LocalDate.class;
    }

    @Override
    public String toString(Object object) {
        if(Optional.ofNullable(object).isPresent()) {
            return ((LocalDate)object).format(DateTimeFormatter.ISO_DATE);
        }
        return null;
    }
}
