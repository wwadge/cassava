package cassava.csv.core;

import lombok.Setter;
import org.apache.commons.lang3.reflect.FieldUtils;
import cassava.csv.core.exceptions.ConfigurationException;
import cassava.csv.core.exceptions.ConversionException;
import cassava.csv.core.typemappers.CustomTypeMapper;
import cassava.csv.core.typemappers.TypeMapper;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.Reader;
import java.lang.reflect.Field;
import java.util.*;
import java.util.function.Consumer;

/**
 * @author Andrew Vella
 * @since 03/11/15.
 */
public class Mapper {
    @Setter
    private String delimiter = ",";

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
        Reflections reflections = new Reflections(ClasspathHelper.forJavaClassPath());
        detectTypeMappers(reflections);
        detectMappedClasses(reflections);
    }
    /**
     * Detects and caches all classes annotated with the @CsvType annotation using the Reflections context.
     * @param reflections Context defined from the defined packages to scan.
     */
    private void detectMappedClasses(Reflections reflections) {
        Set<Class<?>> annotatedClasses = reflections.getTypesAnnotatedWith(CsvType.class);
        for (Class<?> clazz : annotatedClasses) {
            List<Field> fields = FieldUtils.getFieldsListWithAnnotation(clazz, CsvField.class);
            MapperFactory.knownAnnotatedClasses.put(clazz, fields);
        }
    }

    /**
     * Detects and caches all classes which implement the TypeMapper interface.
     * @param reflections
     * @throws ConfigurationException
     */
    private void detectTypeMappers(Reflections reflections) throws ConfigurationException {
        Set<Class<? extends TypeMapper>> classes = reflections.getSubTypesOf(TypeMapper.class);
        for (Class<? extends TypeMapper> clazz : classes) {
            try {
                if(!clazz.isInterface()) {
                    TypeMapper mapper = clazz.newInstance();
                    if(!MapperFactory.typeMappers.containsKey(mapper.getReturnType())
                            || (MapperFactory.typeMappers.containsKey(mapper.getReturnType()) && clazz.getSuperclass() == CustomTypeMapper.class))
                    MapperFactory.typeMappers.put(mapper.getReturnType(), clazz);
                }

            } catch (InstantiationException | IllegalAccessException e) {
                throw new ConfigurationException("Unable to configure type mappers",e);
            }
        }
    }

    /**
     *
     * @param reader Reader from which to read data
     * @param classToMap Class to which to map data
     * @param ignoreHeaders Boolean determining if headers should be ignored. Should be set to true if positions are being used.
     * @param <T> Type of class for classToMap
     * @return Iterator of type T
     * @throws ConversionException
     */
    public <T> Iterator<T> map(Reader reader, Class<T> classToMap,boolean ignoreHeaders) throws ConversionException {
        List<T> results = new ArrayList<>();
        map(reader,classToMap,ignoreHeaders,results::add);
        return results.iterator();
    }

    /**
     *
     * @param reader Reader from which to read data
     * @param classToMap Class to which to map data
     * @param ignoreHeaders Boolean determining if headers should be ignored. Should be set to true if positions are being used.
     * @param function Consumer function which needs to be used on the mapped data
     * @param <T> Type of class for classToMap
     * @throws ConversionException
     */
    public <T> void map(Reader reader, Class<T> classToMap, boolean ignoreHeaders, Consumer<T> function) throws ConversionException {

        if (!Optional.ofNullable(MapperFactory.knownAnnotatedClasses.get(classToMap)).isPresent()) {
            throw new ConversionException("Unknown class. Please annotate with @CsvType");
        }

        try (BufferedReader bufferedReader = (reader instanceof  BufferedReader ? (BufferedReader) reader : new BufferedReader(reader))) {
            String line;
            Map<Integer, String> headers = new HashMap<>();
            int lineNo = -1;

            while ((line =  bufferedReader.readLine()) != null) {
                lineNo++;
                String[] values = line.split(delimiter);
                if (lineNo == 0 & !ignoreHeaders) {
                    //Extract a map of position / header names
                    headers = extractHeaders(values,headers);
                } else {
                    //map values to pojos
                    function.accept(MapperFactory.mapLineValues(classToMap, values, headers));
                }
            }

        } catch (IOException e) {
            throw new ConversionException("Unable to read from Reader",e);
        }
    }

    /**
     * Populate a map between the header position and the actual header name
     * @param headers String[] containing the header names
     * @param map Map which needs to be populated
     * @return Populated map with header positions and header names.
     */
    private Map<Integer, String> extractHeaders(String[] headers, Map<Integer, String> map ) {
        for (int i = 0; i < headers.length; i++) {
            map.put(i, headers[i]);
        }
        return map;
    }


}
