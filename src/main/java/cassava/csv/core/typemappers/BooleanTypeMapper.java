package cassava.csv.core.typemappers;

import java.util.Optional;

/**
 * @author Andrew Vella
 * @since 02/11/15.
 */
public class BooleanTypeMapper implements TypeMapper{

    @Override
    public Boolean fromString(String value) {
        if(!Optional.ofNullable(value).isPresent() || value.isEmpty()) {
            return null;
        }
        return Boolean.valueOf(value);
    }

    @Override
    public Class getReturnType() {
        return Boolean.class;
    }

    @Override
    public String toString(Object object) {
        if(!Optional.ofNullable(object).isPresent()) {
            return null;
        }
        return object.toString();
    }
}
