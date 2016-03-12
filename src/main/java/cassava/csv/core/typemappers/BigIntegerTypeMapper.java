package cassava.csv.core.typemappers;


import java.math.BigInteger;
import java.util.Optional;

/**
 * Converter for <code>BigInteger</code> objects.<br>
 *
 * @author Wallace WAdge
 */
public class BigIntegerTypeMapper implements TypeMapper {

    public final synchronized BigInteger fromString(String value) {
        if (!Optional.ofNullable(value).isPresent() || value.isEmpty()) {
            return null;
        }
        return new BigInteger(value);
    }


    public final synchronized String toString(Object value) {
        return value.toString();
    }

    @Override
    public Class getReturnType() {
        return BigInteger.class;
    }
}
