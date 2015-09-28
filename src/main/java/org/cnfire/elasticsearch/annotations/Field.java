package org.cnfire.elasticsearch.annotations;

import org.cnfire.elasticsearch.data.FieldIndex;
import org.cnfire.elasticsearch.data.FieldType;

import java.lang.annotation.*;

/**
 * Created by jack.zhu on 15-9-22.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
@Inherited()
public @interface Field {
    //字段的类型，如int,string等，通常情况下建议使用string
    FieldType type() default FieldType.String;
    //是否对该字段进行索引操作（添加记录时对该字段建立索引）
    FieldIndex index() default FieldIndex.analyzed;
    //是否对该字段进行索引查询操作
    boolean stored() default true;
    /*
     指定该字段使用的分析器(analyzer)（默认情况下使用IK分词器）
     （包括数据插入时建立索引和查询时所用的analyzer均为同一analyzer）
     */
    String analyzer() default "keyword";

//    String searchAnalyzer() default "";
//
//    String indexAnalyzer() default "";
}
