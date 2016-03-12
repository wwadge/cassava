package cassava.csv.core.typemappers;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Optional;

/**
 * @author Wallace Wadge
 * @since 02/11/15.
 */
public class LocalDateTimeTypeMapper implements TypeMapper {

    @Override
    public LocalDateTime fromString(String value) {
        if (Optional.ofNullable(value).isPresent() && !value.isEmpty()) {
            return LocalDateTime.parse(value, DateTimeFormatter.ISO_DATE_TIME);
        }
        return null;
    }

    @Override
    public Class getReturnType() {
        return LocalDate.class;
    }

    @Override
    public String toString(Object object) {
        if (Optional.ofNullable(object).isPresent()) {
            return ((LocalDateTime) object).format(DateTimeFormatter.ISO_DATE_TIME);
        }
        return null;
    }
}
