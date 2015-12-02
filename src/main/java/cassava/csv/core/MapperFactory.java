package cassava.csv.core;

import cassava.csv.core.exceptions.ConversionException;
import cassava.csv.core.typemappers.TypeMapper;
import com.google.common.reflect.TypeToken;
import org.springframework.util.ReflectionUtils;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;

/**
 * @author Andrew Vella
 * @since 03/11/15.
 */
public class MapperFactory {

    protected static Map<Class, Class<? extends TypeMapper>> typeMappers = new HashMap<>();

    protected static Map<Class, List<Field>> knownAnnotatedClasses = new HashMap<>();

    public static <T> T mapLineValues(Function<List<CsvDataField>, Class> functionMap, String[] stringValues, Map<Integer, String> headerMap) {
        Object o = null;
        List<CsvDataField> csvDataFieldList = mapCsvDataList(stringValues, headerMap);
        Class classToMapTo = functionMap.apply(csvDataFieldList);
        for (CsvDataField csvDataField : csvDataFieldList) {
            o = mapToPojo(classToMapTo, csvDataField, o);
        }
        return (T) o;
    }


    private static List<CsvDataField> mapCsvDataList(String[] stringValues, Map<Integer, String> headerMap) {
        List<CsvDataField> csvDataFieldList = new ArrayList<>();
        for(Integer key: headerMap.keySet()){
            String value = null;
            try {
                value = stringValues[key];
            } catch (IndexOutOfBoundsException ignored) {
            }
            csvDataFieldList.add(new CsvDataField(headerMap.get(key),value,key));
        }
        return csvDataFieldList;
    }

    private static Object mapToPojo(Class classType, CsvDataField csvDataField, Object instanceToPopulate) {

        Object result;
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
        return mapToField(classType, csvDataField, result);

    }

    private static Collection instansiateList(Class classType) {
        if (List.class.getSimpleName().equals(classType.getSimpleName())) {
            return new ArrayList<>();
        } else if (Set.class.getSimpleName().equals(classType.getSimpleName())) {
            return new HashSet<>();
        }
        throw new ConversionException("Unable to create new instance of " + classType.getSimpleName());
    }

    private static Object mapToField(Class classType, CsvDataField csvDataField, Object instanceToPopulate) throws ConversionException {
        List<Field> fields = knownAnnotatedClasses.get(classType);
        if(fields == null) {
            throw new ConversionException("Unknown field mapping for Class:" + classType.getSimpleName());
        }

        if(!csvDataField.getFieldValue().isEmpty()) {
            List<Field> embeddedFields = new ArrayList<>();
            for (Field field : fields) {
                CsvField csvFieldAnnotation = field.getDeclaredAnnotation(CsvField.class);
                //Check that the field type is a known type. If it isn't store it in a separate list to be processed later.
                if (!typeMappers.containsKey(field.getType())) {
                    embeddedFields.add(field);
                }
                //Extract annotation values and attempt to match using either header name or column position
                String headerName = csvFieldAnnotation.headerName();
                int columnPosition = csvFieldAnnotation.columnPosition();

                if (headerName != null && headerName.equalsIgnoreCase(csvDataField.getHeaderName())
                        ||
                        (columnPosition != -1 && csvDataField.getFieldPosition() == columnPosition)
                        || (headerName == null || headerName.isEmpty() && columnPosition == -1 && field.getName().equals(csvDataField.getHeaderName()))) {
                    ReflectionUtils.makeAccessible(field);
                    populateValue(field, instanceToPopulate, csvDataField);
                }
            }
            return populateEmbeddedField(embeddedFields, instanceToPopulate, csvDataField);
        }
       return instanceToPopulate;
    }

    private static void populateListElement(Field field, CsvDataField csvDataField, Object instanceToPopulate) throws ConversionException {

        try {
            Class<?> genericTypeClass = TypeToken.of(field.getGenericType()).resolveType(
                    Class.forName(field.getType().getTypeName()).getTypeParameters()[0]).getRawType();
            ReflectionUtils.makeAccessible(field);
            Collection embeddedObject = (Collection) field.get(instanceToPopulate);
            Object listElementToPopulate = null;
            if (embeddedObject == null) {
                embeddedObject = instansiateList(field.getType());
            } else {
                Optional listElement = embeddedObject.stream().findFirst();
                if(listElement.isPresent()) {
                    listElementToPopulate = listElement.get();
                }
            }
            Object result = mapToPojo(genericTypeClass, csvDataField, listElementToPopulate);
            if (result != null) {
                embeddedObject.clear();
                embeddedObject.add(result);
            }
            if (!embeddedObject.isEmpty()) {
                ReflectionUtils.makeAccessible(field);
                ReflectionUtils.setField(field, instanceToPopulate, embeddedObject);
            }

        } catch (Exception e) {
            throw new ConversionException("Unable to populate list element",e);
        }


    }

    private static Object populateEmbeddedField(List<Field> embeddedFields, Object instanceToPopulate, CsvDataField csvDataField) throws ConversionException {
        for (Field field : embeddedFields) {
            Class<?> fieldClassType = field.getType();
            if (Collection.class.isAssignableFrom(fieldClassType)) {
                populateListElement(field, csvDataField, instanceToPopulate);
            } else {
                try {
                    ReflectionUtils.makeAccessible(field);
                    //Get the instances of the embedded object in the original parent class (instanceToPopulate)
                    Object embeddedObject = field.get(instanceToPopulate);
                    //Recursive call to populate the embedded object.
                    Object result = mapToPojo(fieldClassType, csvDataField, embeddedObject);
                    ReflectionUtils.makeAccessible(field);
                    ReflectionUtils.setField(field, instanceToPopulate, result);
                } catch (IllegalAccessException e) {
                    throw new ConversionException("Unable to set field of type " + fieldClassType.getSimpleName());
                }
            }

        }
        return instanceToPopulate;

    }

    private static void populateValue(Field field, Object instance, CsvDataField csvDataField) throws ConversionException {
        Object mappedValue;
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
