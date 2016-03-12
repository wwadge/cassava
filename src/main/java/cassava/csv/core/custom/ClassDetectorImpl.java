package cassava.csv.core.custom;

import cassava.csv.core.CsvType;
import cassava.csv.core.typemappers.CustomTypeMapper;
import cassava.csv.core.typemappers.TypeMapper;
import org.reflections.Reflections;
import org.reflections.util.ClasspathHelper;

import java.util.Set;

/**
 * @author Andrew Vella
 * @since 22/12/15.
 */
public class ClassDetectorImpl extends AbstractClassDetector {

    private Reflections reflections;

    public ClassDetectorImpl() {
        reflections = new Reflections(ClasspathHelper.forJavaClassPath());
    }

    @Override
    public Set<Class<?>> detectClassesAnnotatedWithCsvType() {
        return reflections.getTypesAnnotatedWith(CsvType.class);
    }

    @Override
    public Set<Class<? extends TypeMapper>> detectDefaultTypeMappers() {
       return reflections.getSubTypesOf(TypeMapper.class);
    }

    @Override
    public Set<Class<? extends CustomTypeMapper>> detectCustomTypeMappers() {
        return reflections.getSubTypesOf(CustomTypeMapper.class);
    }
}

