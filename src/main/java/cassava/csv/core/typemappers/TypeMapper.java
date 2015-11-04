package cassava.csv.core.typemappers;

/**
 * Created by andrew on 02/11/15.
 */
public interface TypeMapper {
    Object fromString(String value);
    Class getReturnType();
}
