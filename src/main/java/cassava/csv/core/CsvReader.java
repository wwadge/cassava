package cassava.csv.core;

import cassava.csv.core.exceptions.ConversionException;
import cassava.csv.core.typemappers.TypeMapper;
import cassava.csv.core.utils.ReflectionUtils;
import com.google.common.reflect.TypeToken;

import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Function;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * @author Andrew Vella
 * @since 03/11/15.
 */
public class CsvReader extends AbstractCsvMapper {

    private final static String MAP_KEY_REGEX = "\\[(.*?)\\]";

    public <T> T mapLineValues(Function<List<CsvDataField>, Class> functionMap, String[] stringValues, Map<Integer, String> headerMap) {
        Object o = null;
        List<CsvDataField> csvDataFieldList = mapCsvDataList(stringValues, headerMap);
        Class classToMapTo = functionMap.apply(csvDataFieldList);
        for (CsvDataField csvDataField : csvDataFieldList) {
            o = mapToPojo(classToMapTo, csvDataField, o);
        }
        return (T) o;
    }


    private List<CsvDataField> mapCsvDataList(String[] stringValues, Map<Integer, String> headerMap) {
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

    private Object mapToPojo(Class classType, CsvDataField csvDataField, Object instanceToPopulate) {

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

    private Collection instansiateList(Class classType) {
        if (List.class.getSimpleName().equals(classType.getSimpleName())) {
            return new ArrayList<>();
        } else if (Set.class.getSimpleName().equals(classType.getSimpleName())) {
            return new HashSet<>();
        }
        throw new ConversionException("Unable to create new instance of " + classType.getSimpleName());
    }

    private Object mapToField(Class classType, CsvDataField csvDataField, Object instanceToPopulate) throws ConversionException {
        List<Field> fields = getKnownAnnotatedClasses().get(classType);
        if (!Optional.ofNullable(fields).isPresent()) {
            throw new ConversionException("Unknown field mapping for Class:" + classType.getSimpleName());
        }

        if(Optional.ofNullable(csvDataField.getFieldValue()).isPresent() && !csvDataField.getFieldValue().isEmpty()) {
            List<Field> embeddedFields = new ArrayList<>();
            for (Field field : fields) {
                CsvField csvFieldAnnotation = field.getDeclaredAnnotation(CsvField.class);
                //Check that the field type is a known type. If it isn't store it in a separate list to be processed later.
                if (!getTypeMappers().containsKey(field.getType())) {
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


    private void populateMapElement(Field field, CsvDataField csvDataField, Object instanceToPopulate) throws ConversionException {

        if(Optional.ofNullable(csvDataField.getHeaderName()).isPresent()
                && csvDataField.getHeaderName().matches(MAP_KEY_REGEX)) {

            try {
                ReflectionUtils.makeAccessible(field);
                Map embeddedObject = (Map) field.get(instanceToPopulate);
                if (embeddedObject == null) {
                    embeddedObject = new HashMap<>();
                }
                Matcher matcher = Pattern.compile(MAP_KEY_REGEX).matcher(csvDataField.getHeaderName());
                if(matcher.find()) {
                    String mapKey = matcher.group(1);
                    embeddedObject.put(mapKey,csvDataField.getFieldValue());

                    if (!embeddedObject.isEmpty()) {
                        ReflectionUtils.makeAccessible(field);
                        ReflectionUtils.setField(field, instanceToPopulate, embeddedObject);
                    }
                }
            } catch (IllegalAccessException e) {
                throw new ConversionException("Unable to access field", e);
            }
        }

    }


    private void populateListElement(Field field, CsvDataField csvDataField, Object instanceToPopulate) throws ConversionException {

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

    private Object populateEmbeddedField(List<Field> embeddedFields, Object instanceToPopulate, CsvDataField csvDataField) throws ConversionException {
        for (Field field : embeddedFields) {
            Class<?> fieldClassType = field.getType();
            if (Collection.class.isAssignableFrom(fieldClassType)) {
                populateListElement(field, csvDataField, instanceToPopulate);
            } else if(Map.class.equals(fieldClassType)) {
                populateMapElement(field,csvDataField,instanceToPopulate);

            } else  {
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

    private void populateValue(Field field, Object instance, CsvDataField csvDataField) throws ConversionException {
        Object mappedValue;
        Class fieldType = field.getType();
        //Attempt to get from cached mappers
        TypeMapper mapper = getTypeMappers().get(fieldType);
        //Use specified
        if (mapper == null) {
            throw new ConversionException("Unknown Type :" + fieldType);
        }
        mappedValue = mapper.fromString(csvDataField.getFieldValue());
        ReflectionUtils.makeAccessible(field);
        ReflectionUtils.setField(field, instance, mappedValue);
    }
}
