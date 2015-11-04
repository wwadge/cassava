package cassava.csv.core;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by andrew on 02/11/15.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.FIELD)
public @interface CsvField {

    String headerName() default "";
    int columnPosition() default -1;
//    Class<? extends TypeMapper> typeMapper() default DefaultTypeMapper.class;
//    boolean embedded() default false;


//    TypeMapper typeMapper();
}
