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
            return false;
        }
        return Boolean.valueOf(value);
    }

    @Override
    public Class getReturnType() {
        return Boolean.class;
    }
}
