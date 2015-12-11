package cassava.csv.core.typemappers;

import cassava.csv.core.exceptions.ConversionException;

import java.util.Optional;

/**
 * @author Andrew Vella
 * @since 04/11/15.
 */
public class IntegerTypeMapper implements TypeMapper {
    @Override
    public Integer fromString(String value) {
        if (!Optional.ofNullable(value).isPresent() || value.isEmpty()) {
            return null;
        }
        try {
            return Integer.valueOf(value);
        } catch (NumberFormatException e) {
            if (value.charAt(0) == '+') {
                return fromString(value.substring(1));
            }
            throw new ConversionException("Wrong Integer format: " + value);
        }
    }

    @Override
    public Class getReturnType() {
        return Integer.class;
    }

    @Override
    public String toString(Object object) {
        if(!Optional.ofNullable(object).isPresent()) {
            return null;
        }
        return Integer.toString((Integer)object);
    }
}
