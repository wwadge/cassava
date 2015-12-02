package cassava.csv.core.typemappers;

/**
 * @author Andrew Vella
 * @since 04/11/15.
 */
public interface TypeMapper {
    Object fromString(String value);
    Class getReturnType();
}
