package cassava.csv.core.typemappers;


import cassava.csv.core.exceptions.ConversionException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.util.Locale;
import java.util.Optional;

/**
 * Converter for <code>BigDecimal</code> objects.<br>
 * The format consists of two <code>String</code>s. The first denotes the {@link Locale} and the second is a
 * pattern as used by {@link DecimalFormat}.<br>
 * <p>
 * It is thread-safe (the access to the non-thread-safe {@link DecimalFormat} is synchronized).
 *
 * @author Norman Lahme-Huetig
 */
public class BigDecimalTypeMapper implements TypeMapper {
    /**
     * The default format which is used when no format is explicitly given.
     */
    private static final String[] DEFAULT_FORMAT = {"en", "#0.00"};

    private static final FieldPosition FIELD_POSITION = new FieldPosition(0);


    private DecimalFormat getDecimalFormat() {
        try {
            DecimalFormat decimalFormat = new DecimalFormat();
            decimalFormat.setParseBigDecimal(true);
            return decimalFormat;
        } catch (Exception e) {
            throw new ConversionException("Could not create a " + this.getClass().getName(), e);
        }
    }


    public final synchronized BigDecimal fromString(String value) {
        if (!Optional.ofNullable(value).isPresent() || value.isEmpty()) {
            return null;
        }
        try {
            Object result = getDecimalFormat().parseObject(value);
            if (result instanceof BigDecimal) {
                return (BigDecimal) result;
            } else {
                return new BigDecimal(((Double) result)).setScale(getDecimalFormat()
                        .getMaximumFractionDigits(), BigDecimal.ROUND_HALF_UP);
            }
        } catch (ParseException e) {
            throw new ConversionException("Wrong BigDecimal format " + value);
        }
    }


    public final synchronized String toString(Object value) {
        if (value == null) {
            return null;
        }
        return getDecimalFormat().format(value, new StringBuffer(), FIELD_POSITION).toString();
    }

    @Override
    public Class getReturnType() {
        return BigDecimal.class;
    }
}
