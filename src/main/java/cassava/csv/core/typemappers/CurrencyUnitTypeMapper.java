package cassava.csv.core.typemappers;


import com.google.common.collect.Maps;

import javax.money.CurrencyUnit;
import javax.money.Monetary;
import java.util.Currency;
import java.util.Map;
import java.util.Optional;

/**
 * Converter for <code>CurrencyUnit</code> objects. Accepts currency numeric code<br>
 *
 * @author Wallace WAdge
 */
public class CurrencyUnitTypeMapper implements TypeMapper {
    final static Map<Integer, CurrencyUnit> currencyMap = Maps.newHashMap();       // for currency lookups

    static {
        for (Currency currency : Currency.getAvailableCurrencies()) {
            currencyMap.put(currency.getNumericCode(), Monetary.getCurrency(currency.getCurrencyCode()));
        }
    }

    public final synchronized CurrencyUnit fromString(String value) {
        if (!Optional.ofNullable(value).isPresent() || value.isEmpty()) {
            return null;
        }

        return currencyMap.get(Integer.valueOf(value));
    }


    public final synchronized String toString(Object value) {
        return ((CurrencyUnit) value).getCurrencyCode();
    }

    @Override
    public Class getReturnType() {
        return CurrencyUnit.class;
    }
}
