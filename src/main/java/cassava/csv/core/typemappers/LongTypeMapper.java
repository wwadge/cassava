package cassava.csv.core.typemappers;

import cassava.csv.core.exceptions.ConversionException;

import java.util.Optional;

/**
 * @author Andrew Vella
 * @since 04/11/15.
 */
public class LongTypeMapper implements TypeMapper{

    @Override
    public Long fromString(String value) {
        if (!Optional.ofNullable(value).isPresent() || value.isEmpty()) {
            return null;
        }
        try {
            return new Long(value);
        } catch (NumberFormatException e) {
            if (value.charAt(0) == '+') {
                return fromString(value.substring(1));
            }
            throw new ConversionException("Wrong Long format: " + value);
        }
    }

    @Override
    public Class getReturnType() {
        return Long.class;
    }
}
