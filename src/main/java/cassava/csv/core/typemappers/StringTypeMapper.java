package cassava.csv.core.typemappers;

/**
 * @author Andrew Vella
 * @since 04/11/15.
 */
public class StringTypeMapper implements TypeMapper {

    @Override
    public String fromString(String value) {
        return value;
    }

    @Override
    public Class getReturnType() {
        return String.class;
    }

    @Override
    public String toString(Object object) {
        return (String) object;
    }
}
