package cassava.csv.core.typemappers;

/**
 * Created by andrew on 02/11/15.
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
}
