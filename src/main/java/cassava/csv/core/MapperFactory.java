package cassava.csv.core;

import cassava.csv.core.typemappers.TypeMapper;
import cassava.csv.core.exceptions.ConversionException;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;

/**
 * @author Andrew Vella
 * @since 03/11/15.
 */
public class MapperFactory {

    protected static Map<Class, Class <? extends TypeMapper>> typeMappers = new HashMap<>();

    protected static Map<Class, List<Field>> knownAnnotatedClasses = new HashMap<>();

    public static <T> T mapLineValues(Class clazzType, String[] stringValues, Map<Integer, String> headerMap) {
        Object o = null;
        List<CsvDataField> csvDataFieldList = mapCsvDataList(stringValues, headerMap);
        for (CsvDataField csvDataField : csvDataFieldList) {
            o = mapToPojo(clazzType, csvDataField, o);
        }
        return (T) o;
    }


    private static List<CsvDataField> mapCsvDataList(String[] stringValues, Map<Integer, String> headerMap) {
        List<CsvDataField> csvDataFieldList = new ArrayList<>();
        for (int i = 0; i < stringValues.length; i++) {
            csvDataFieldList.add(new CsvDataField(headerMap.get(i), stringValues[i], i));
        }
        return csvDataFieldList;
    }

    private static Object mapToPojo(Class classType, CsvDataField csvDataField, Object instanceToPopulate) {

        Object result = null;
        //Create  new instance if null
        if (!Optional.ofNullable(instanceToPopulate).isPresent()) {
            try {
                result = classType.newInstance();
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ConversionException("Unable to create new instance of " + classType.getSimpleName());
            }
        } else {
            result = instanceToPopulate;
        }

        return mapToField(classType,csvDataField,result);

    }

    private static Object mapToField(Class classType, CsvDataField csvDataField, Object instanceToPopulate) throws ConversionException {
        List<Field> fields = knownAnnotatedClasses.get(classType);
        List<Field> embeddedFields = new ArrayList<>();
        boolean fieldMatchedAndPopulated = false;
        for (Field field : fields) {
            CsvField csvFieldAnnotation = field.getDeclaredAnnotation(CsvField.class);
            //Check that the field type is a known type. If it isn't store it in a separate list to be processed later.
            if(!typeMappers.containsKey(field.getType())) {
                embeddedFields.add(field);
            }
            //Extract annotation values and attempt to match using either header name or column position
            String headerName = csvFieldAnnotation.headerName();
            int columnPosition = csvFieldAnnotation.columnPosition();

            if ((headerName != null && headerName.equalsIgnoreCase(csvDataField.getHeaderName()))
                    ||
                    (columnPosition != -1 && csvDataField.getFieldPosition() == columnPosition)) {
                ReflectionUtils.makeAccessible(field);
                populateValue(field, instanceToPopulate, csvDataField);
                fieldMatchedAndPopulated = true;
                break;
            }
        }
        //If the field has not been matched or populated with the header names/column positions trigger recursive parsing
        if(!fieldMatchedAndPopulated) {
            instanceToPopulate = populateEmbeddedField(embeddedFields,instanceToPopulate,csvDataField);
        }
        return instanceToPopulate;
    }

    private static Object populateEmbeddedField(List<Field> embeddedFields, Object instanceToPopulate, CsvDataField csvDataField) throws ConversionException {
        for(Field field : embeddedFields) {
            Class<?> fieldClassType = field.getType();
            try {
                ReflectionUtils.makeAccessible(field);
                //Get the instances of the embedded object in the original parent class (instanceToPopulate)
                Object embeddedObject = field.get(instanceToPopulate);
                //Recursive call to populate the embedded object.
                Object result = mapToPojo(fieldClassType,csvDataField,embeddedObject);
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, instanceToPopulate, result);
            } catch (IllegalAccessException e) {
                throw new ConversionException("Unable to set field of type " + fieldClassType.getSimpleName());
            }
        }
        return instanceToPopulate;

    }

    private static void populateValue(Field field, Object instance, CsvDataField csvDataField) throws ConversionException {
        Object mappedValue = null;
        Class fieldType = field.getType();
        //Attempt to get from cached mappers
        Class<? extends TypeMapper> mapperClass = typeMappers.get(fieldType);
        //Use specified
        if (mapperClass == null) {
            throw new ConversionException("Unknown Type :" + fieldType);
        }
        try {
            TypeMapper mapper = mapperClass.newInstance();
            mappedValue = mapper.fromString(csvDataField.getFieldValue());
        } catch (InstantiationException | IllegalAccessException e) {
            throw new ConversionException("Unable to map value :" + csvDataField.getFieldValue() + " to field type " + fieldType.getSimpleName() + "using TypeMapper: " + mapperClass.getSimpleName());
        }
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, instance, mappedValue);
    }
}
