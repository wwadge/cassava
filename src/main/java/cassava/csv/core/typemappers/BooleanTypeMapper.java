package cassava.csv.core.typemappers;

/**
 * Created by andrew on 02/11/15.
 */
public class BooleanTypeMapper implements TypeMapper{

    @Override
    public Boolean fromString(String value) {
        if(value == null) {
            return false;
        }
        return Boolean.valueOf(value);
    }

    @Override
    public Class getReturnType() {
        return Boolean.class;
    }
}
