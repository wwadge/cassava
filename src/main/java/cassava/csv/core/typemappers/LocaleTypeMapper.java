package cassava.csv.core.typemappers;


import com.google.common.collect.BiMap;
import com.google.common.collect.HashBiMap;
import com.google.common.collect.ImmutableBiMap;
import com.neovisionaries.i18n.CountryCode;

import java.util.Locale;
import java.util.Optional;

/**
 * Converter for <code>CurrencyUnit</code> objects. Accepts currency numeric code<br>
 *
 * @author Wallace WAdge
 */
public class LocaleTypeMapper implements TypeMapper {
    public static ImmutableBiMap<String, Locale> countryMap;

    static {
        String[] locales = Locale.getISOCountries();

        BiMap<String, Locale> map = HashBiMap.create();

        for (String countryCode : locales) {
            Locale obj = new Locale("", countryCode);
            map.put(obj.getCountry(), obj);
        }

        countryMap = ImmutableBiMap.copyOf(map);

    }


    public final synchronized Locale fromString(String value) {
        if (!Optional.ofNullable(value).isPresent() || value.isEmpty()) {
            return null;
        }

        try {
            return countryMap.get(CountryCode.getByCode(Integer.parseInt(value)).getAlpha2());
        } catch (Exception e) {
            return null;
        }

    }


    public final synchronized String toString(Object value) {
        return countryMap.inverse().get(value);
    }

    @Override
    public Class getReturnType() {
        return Locale.class;
    }
}
