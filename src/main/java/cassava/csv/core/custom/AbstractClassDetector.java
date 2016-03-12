package cassava.csv.core.custom;

import cassava.csv.core.typemappers.*;
import com.google.common.collect.Sets;

import java.util.Set;

/**
 * @author Wallace Wadge
 * @since 22/12/15.
 */
public abstract class AbstractClassDetector implements ClassDetector {


    @Override
    public Set<Class<? extends TypeMapper>> detectDefaultTypeMappers() {
        return Sets.newHashSet(BigDecimalTypeMapper.class, BooleanTypeMapper.class, IntegerTypeMapper.class,
                LocalDateTypeMapper.class, LocalDateTimeTypeMapper.class, LongTypeMapper.class, StringTypeMapper.class,
                BigIntegerTypeMapper.class, CurrencyUnitTypeMapper.class, LocaleTypeMapper.class);
    }

    @Override
    public Set<Class<?>> detectClassesAnnotatedWithCsvType() {
        return Sets.newHashSet();
    }

    @Override
    public Set<Class<? extends CustomTypeMapper>> detectCustomTypeMappers() {
        return Sets.newHashSet();
    }
}

