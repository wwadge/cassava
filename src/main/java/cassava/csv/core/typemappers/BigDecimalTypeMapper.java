package cassava.csv.core.typemappers;

/**
 * Created by andrew on 02/11/15.
 */

import cassava.csv.core.exceptions.ConversionException;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.FieldPosition;
import java.text.ParseException;
import java.util.Locale;

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

    private final DecimalFormat decimalFormat;

    /**
     * Constructs a new <code>BigDecimalTypeMapper</code>.<br>
     * If no format is given, the default format (see {@link #getDefaultFormat()}) is used.
     *
     * @param configuration the configuration
     * @throws ConversionException if the given format is not valid.
     */
    public BigDecimalTypeMapper() {
        try {
            this.decimalFormat = new DecimalFormat();
            this.decimalFormat.setParseBigDecimal(true);
        } catch (Exception e) {
            throw new ConversionException("Could not create a " + this.getClass().getName(), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final synchronized BigDecimal fromString(String value) {
        if (value == null || value.length() == 0) {
            return null;
        }
        try {
            Object result = this.decimalFormat.parseObject(value);
            if (result instanceof BigDecimal) {
                return (BigDecimal) result;
            } else {
                return new BigDecimal(((Double) result).doubleValue()).setScale(this.decimalFormat
                        .getMaximumFractionDigits(), BigDecimal.ROUND_HALF_UP);
            }
        } catch (ParseException e) {
            throw new ConversionException("Wrong BigDecimal format " + value);
        }
    }

    /**
     * {@inheritDoc}
     */
    public final synchronized String toString(Object value) {
        if (value == null) {
            return null;
        }
        return this.decimalFormat.format(value, new StringBuffer(), FIELD_POSITION).toString();
    }

    @Override
    public Class getReturnType() {
        return BigDecimal.class;
    }
}
