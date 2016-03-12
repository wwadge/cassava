package cassava.csv.core.custom;

import cassava.csv.core.typemappers.CustomTypeMapper;
import cassava.csv.core.typemappers.TypeMapper;

import java.util.Set;

/**
 * @author Andrew Vella
 * @since 22/12/15.
 */
public interface ClassDetector {


    /**
     * Returns a set of Classes annotated with @CsvType annotation
     *
     * @return set of detected classes
     */
    Set<Class<?>> detectClassesAnnotatedWithCsvType();

    /**
     * Returns a set of Classes which are subtypes of TypeMapper class
     *
     * @return set of type mappers
     */
    Set<Class<? extends TypeMapper>> detectDefaultTypeMappers();

    /**
     * Returns a set of Classes which are subtypes of CustomTypeMapper class
     *
     * @return set of custom type mappers
     */
    Set<Class<? extends CustomTypeMapper>> detectCustomTypeMappers();

}


