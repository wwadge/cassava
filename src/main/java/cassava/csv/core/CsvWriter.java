package cassava.csv.core;

import cassava.csv.core.exceptions.ConversionException;
import cassava.csv.core.exceptions.NoTypeMapperFoundException;
import cassava.csv.core.typemappers.TypeMapper;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Andrew Vella
 * @since 11/12/15.
 */
public class CsvWriter {

    protected static Map<Class, Class<? extends TypeMapper>> typeMappers = new HashMap<>();
    protected static Map<Class, List<Field>> knownAnnotatedClasses = new HashMap<>();

    private static final String delimiter = ",";


    public static String mapObject(Object objectToMap, boolean emptyPlaceHolders) {
        try {
            //Attempt to map using the known type mappers
            try {
                return extractValueFromTypeMappers(objectToMap);
            } catch (NoTypeMapperFoundException e) {
                //If unable to map using type mappers extract each field and begin extractring strings.
                StringBuilder stringBuilder = new StringBuilder();
                List<Field> fields = knownAnnotatedClasses.get(objectToMap.getClass());
                Iterator<Field> fieldIterator = fields.iterator();
                while (fieldIterator.hasNext()) {
                    Field field = fieldIterator.next();
                    stringBuilder.append(extractValueFromField(field, objectToMap, emptyPlaceHolders));
                    if (fieldIterator.hasNext()) {
                        stringBuilder.append(delimiter);
                    }
                }
                return stringBuilder.toString();
            }
        } catch (Throwable e) {
            throw new ConversionException("Unable to convert requested object", e);
        }
    }

    /**
     * Build a string representation of a map object.
     * @param object Map Object
     * @param emptyPlaceHolders Boolean flag determining whether to add [] when a map is empty.
     * @return
     */
    private static String extractMapValues(Object object, boolean emptyPlaceHolders) {
        if (!Optional.ofNullable(object).isPresent()) {
            return emptyPlaceHolders ? "[]" : "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        Map<Object, Object> objectMap = (Map<Object, Object>) object;
        Iterator<Map.Entry<Object, Object>> iterator = objectMap.entrySet().iterator();
        while (iterator.hasNext()) {
            stringBuilder.append("[");
            stringBuilder.append(mapObject(iterator.next().getKey(), emptyPlaceHolders));
            stringBuilder.append(",");
            stringBuilder.append(mapObject(iterator.next().getValue(), emptyPlaceHolders));
            stringBuilder.append("]");
            if (iterator.hasNext()) {
                stringBuilder.append(delimiter);
            }
        }
        return stringBuilder.toString();
    }


    /**
     *
     * @param object List Object
     * @param emptyPlaceHolders Boolean flag determining whether to add [] when a list is empty.
     * @return
     */
    private static String extractListValues(Object object, boolean emptyPlaceHolders) {

        if (!Optional.ofNullable(object).isPresent()) {
            return emptyPlaceHolders ? "[]" : "";
        }
        StringBuilder stringBuilder = new StringBuilder();
        List<Object> objectCollection = (List) object;
        Iterator<Object> iterator = objectCollection.listIterator();
        while (iterator.hasNext()) {
            stringBuilder.append("[");
            stringBuilder.append(mapObject(iterator.next(), emptyPlaceHolders));
            stringBuilder.append("]");
            if (iterator.hasNext()) {
                stringBuilder.append(delimiter);
            }
        }
        return stringBuilder.toString();
    }


    private static String extractValueFromField(Field field, Object sourceObject, boolean emptyPlaceHolders) throws ConversionException {
        try {
            ReflectionUtils.makeAccessible(field);
            Object fieldValueToMap = field.get(sourceObject);
            //Custom handling for Collections/Maps
            if (Collection.class.isAssignableFrom(field.getType())) {
                return extractListValues(fieldValueToMap, emptyPlaceHolders);
            } else if (Map.class.equals(field.getType())) {
                return extractMapValues(fieldValueToMap, emptyPlaceHolders);
            } else {
                //Attempt to map from type mappers
                try {
                    return extractValueFromTypeMappers(fieldValueToMap);
                } catch (NoTypeMapperFoundException e1) {
                    //Recursive call to map object to descent into the fields
                    return mapObject(fieldValueToMap, emptyPlaceHolders);
                }
            }
        } catch (IllegalAccessException ex) {
            throw new ConversionException("Invalid field in  requested object " + field.getName(), ex);
        }
    }

    private static String extractValueFromTypeMappers(Object object) throws NoTypeMapperFoundException {
        if (Optional.ofNullable(object).isPresent()) {
            Class<? extends TypeMapper> mapperClass = typeMappers.get(object.getClass());
            //Use specified
            if (mapperClass == null) {
                throw new NoTypeMapperFoundException("Unknown Type :" + object.getClass());
            }
            try {
                TypeMapper mapper = mapperClass.newInstance();
                return mapper.toString(object);
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ConversionException("Unable to map value :" + object + " using TypeMapper: " + mapperClass.getSimpleName());
            }
        }
        return "";
    }


}
