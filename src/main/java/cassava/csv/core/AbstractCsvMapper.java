package cassava.csv.core;

import cassava.csv.core.typemappers.TypeMapper;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @author Andrew Vella
 * @since 14/12/15.
 */
public abstract class AbstractCsvMapper {

    private static Map<Class, List<Field>> knownAnnotatedClasses = new HashMap<>();
    private static Map<Class, TypeMapper> typeMappers = new HashMap<>();

    private String delimiter = ",";

    public Map<Class, List<Field>> getKnownAnnotatedClasses() {
        return knownAnnotatedClasses;
    }

    public Map<Class, TypeMapper> getTypeMappers() {
        return typeMappers;
    }

    public boolean cacheEmpty() {
        return getKnownAnnotatedClasses().isEmpty() || getTypeMappers().isEmpty();
    }

    public String getDelimiter() {
        return delimiter;
    }

    public void setDelimiter(String delimiter) {
        this.delimiter = delimiter;
    }


}
