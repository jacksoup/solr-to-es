package org.cnfire.elasticsearch.annotations;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by jack.zhu on 15-9-24.
 */
@Target(ElementType.ANNOTATION_TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface Index {
    String name();
    short shards() default 5;//number_of_shards
    short replicas() default 1;//number_of_replicas
    String setting() default "";
}
