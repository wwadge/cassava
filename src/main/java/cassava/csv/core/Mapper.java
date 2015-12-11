package cassava.csv.core;

import cassava.csv.core.exceptions.ConfigurationException;
import cassava.csv.core.exceptions.ConversionException;
import cassava.csv.core.typemappers.CustomTypeMapper;
import cassava.csv.core.typemappers.TypeMapper;
import lombok.Setter;
import org.apache.commons.lang3.reflect.FieldUtils;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;
import java.util.function.Function;

/**
 * @author Andrew Vella
 * @since 03/11/15.
 */
public class Mapper {
    @Setter
    private String delimiter = ",";

    private Reflections reflections;

    public Mapper() throws ConfigurationException {
        init();
    }

    public Mapper(String delimiter) throws ConfigurationException {
        init();
        this.delimiter = delimiter;
    }

    /**
     * Initialises internal class detection and caching
     */
    private void init() {
        reflections = new Reflections(ClasspathHelper.forJavaClassPath());
        populateCache();
    }

    private void populateCache() {
        Map<Class, List<Field>> annotatedClassesAndFields = getAnnotatedClassesAndFields();
        Map<Class, TypeMapper> typeMappers = getTypeMappers();
        //Reader
        CsvReader.knownAnnotatedClasses.putAll(annotatedClassesAndFields);
        CsvReader.typeMappers.putAll(typeMappers);
        //Wrtier
        CsvWriter.knownAnnotatedClasses.putAll(annotatedClassesAndFields);
        CsvWriter.typeMappers.putAll(typeMappers);
    }

    /**
     * Detects and caches all classes annotated with the @CsvType annotation using the Reflections context.
     *
     */
    private Map<Class, List<Field>> getAnnotatedClassesAndFields() {
        HashMap<Class, List<Field>> results = new HashMap<>();
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(CsvType.class);
        for (Class<?> clazz : annotatedClasses) {
            List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, CsvField.class);
            results.put(clazz, fields);
        }
        return results;
    }


    private Map<Class,TypeMapper> getTypeMappers() throws ConfigurationException    {
        Map<Class, TypeMapper> defaultTypeMappers = getDefaultTypeMappers();
        Map<Class, TypeMapper> customTypeMappers = getCustomTypeMappers();
        Map<Class, TypeMapper> aggregatedTypeMappers = new HashMap<>();
        aggregatedTypeMappers.putAll(defaultTypeMappers);
        if(customTypeMappers != null) {
            aggregatedTypeMappers.putAll(customTypeMappers);
        }
        return aggregatedTypeMappers;
    }
    /**
     * Detects and caches all classes which implement the TypeMapper interface.
     *
     * @throws ConfigurationException
     */
    private Map<Class, TypeMapper> getDefaultTypeMappers() throws ConfigurationException {
        Map<Class, TypeMapper> results = new HashMap<>();

        Set<Class<? extends TypeMapper>> classes = reflections.getSubTypesOf(TypeMapper.class);
        for (Class<? extends TypeMapper> clazz : classes) {
            try {
                if (!clazz.isInterface() && (clazz.getSuperclass() == null || clazz.getSuperclass() != CustomTypeMapper.class)) {
                    TypeMapper mapper = clazz.newInstance();
                    if (!results.containsKey(mapper.getReturnType())) {
                        results.put(mapper.getReturnType(), mapper);
                    }
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ConfigurationException("Unable to configure type mappers", e);
            }
        }
        return results;
    }


    public Map<Class,TypeMapper> getCustomTypeMappers() throws ConfigurationException {
        Map<Class, TypeMapper> results = new HashMap<>();
        Set<Class<? extends CustomTypeMapper>> classes = reflections.getSubTypesOf(CustomTypeMapper.class);
        for (Class<? extends CustomTypeMapper> clazz : classes) {
            try {
                TypeMapper mapper = clazz.newInstance();
                if (!results.containsKey(mapper.getReturnType())) {
                    results.put(mapper.getReturnType(), mapper);
                }
            } catch (InstantiationException | IllegalAccessException e) {
                throw new ConfigurationException("Unable to configure type mappers", e);
            }
        }
        return results;
    }


    /**
     * Maps data from the given reader to an Iterator of specified Class Type.
     *
     * @param reader        Reader from which to read data
     * @param classToMap    Class to which to map data
     * @param ignoreHeaders Boolean determining if headers should be ignored. Should be set to true if positions are being used.
     * @param <T>           Type of class for classToMap
     * @return Iterator of type T
     * @throws ConversionException
     */
    public <T> Iterator<T> map(Reader reader, Class<T> classToMap, boolean ignoreHeaders) throws ConversionException {
        List<T> results = new ArrayList<>();
        map(reader, classToMap, ignoreHeaders, results::add);
        return results.iterator();
    }

    /**
     * @param reader        Reader from which to read data
     * @param classToMap    Class to which to map data
     * @param ignoreHeaders Boolean determining if headers should be ignored. Should be set to true if positions are being used.
     * @param function      Function which to action upon the computed result
     * @param <T>
     * @throws ConversionException
     */
    public <T> void map(Reader reader, Class<T> classToMap, boolean ignoreHeaders, Consumer<T> function) throws ConversionException {
        map(reader, classToMap, ignoreHeaders, function, csvDataField -> classToMap);
    }

    /**
     * @param reader        Reader from which to read data
     * @param classToMap    Class to which to map data
     * @param ignoreHeaders Boolean determining if headers should be ignored. Should be set to true if positions are being used.
     * @param function      Consumer function which needs to be used on the mapped data
     * @param <T>           Type of class for classToMap
     * @throws ConversionException
     */
    public <T> void map(Reader reader, Class<T> classToMap, boolean ignoreHeaders, Consumer<T> function, Function<List<CsvDataField>, Class> customMappingFunction) throws ConversionException {

        if (!classToMap.getSimpleName().equals(Object.class.getSimpleName()) && !Optional.ofNullable(CsvReader.knownAnnotatedClasses.get(classToMap)).isPresent()) {
            throw new ConversionException("Unknown class. Please annotate with @CsvType");
        }

        try (BufferedReader bufferedReader = (reader instanceof BufferedReader ? (BufferedReader) reader : new BufferedReader(reader))) {
            String line;
            Map<Integer, String> headers = new HashMap<>();
            int lineNo = -1;

            while ((line = bufferedReader.readLine()) != null) {
                lineNo++;
                String[] values = line.split(delimiter, -1);
                if (lineNo == 0 & !ignoreHeaders) {
                    //Extract a map of position / header names
                    headers = extractHeaders(values, headers);
                } else {
                    //map values to pojos
                    function.accept(CsvReader.mapLineValues(customMappingFunction, values, headers));
                }
            }

        } catch (IOException e) {
            throw new ConversionException("Unable to read from Reader", e);
        }
    }

    /**
     * Actions a custom function on the data output by the CSV converted string.
     *
     * @param objectToMap               Pojo which needs to be converted to CSV String
     * @param populateEmptyPlaceHolders Flag indicating whether to return empty place holders when Collections/Maps are null
     * @param function                  Custom function to action upon the computed result
     */
    public void mapToString(Object objectToMap, boolean populateEmptyPlaceHolders, Consumer<String> function) {
        String result = mapToString(objectToMap, populateEmptyPlaceHolders);
        function.accept(result);
    }

    /**
     * Extracts a CSV String from the Pojo.
     *
     * @param objectToMap               Pojo to be converted
     * @param populateEmptyPlaceHolders Flag indicating whether to return empty place holders when Collections/Maps are null
     * @return String
     * @throws ConversionException
     */
    public String mapToString(Object objectToMap, boolean populateEmptyPlaceHolders) throws ConversionException {
        return CsvWriter.mapObject(objectToMap, populateEmptyPlaceHolders);
    }


    /**
     * Populate a map between the header position and the actual header name
     *
     * @param headers String[] containing the header names
     * @param map     Map which needs to be populated
     * @return Populated map with header positions and header names.
     */
    private Map<Integer, String> extractHeaders(String[] headers, Map<Integer, String> map) {
        for (int i = 0; i < headers.length; i++) {
            map.put(i, headers[i]);
        }
        return map;
    }


}
