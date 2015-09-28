package org.cnfire.elasticsearch.accessor;

import java.util.List;

/**
 * Created by caesar.zhu on 15-8-21.
 * 和ElasticSearch进行交互的底层接口，包括对索引、映射、类型、以及具体记录数据的添加、更新、删除、查询、搜索等操作
 * 目前有两个实现类：
 * (1).AccessorClientImpl: 通过es自带的TransportClient客户端提供的API进行操作
 * (2).AccessorWebImpl: 通过es的原生的restfult web API进行操作
 */
public interface IAccessor {
    /*添加单个对象*/
    <T> boolean add(T model);
    /*批量添加对象*/
    <T> boolean add(List<T> models);
    /*根据ID获得记录*/
    <T> T get(Class<T> t,String id);
    /*根据ID删除记录*/
    boolean delete(String id,Class clazz);
    /*创建索引*/
    boolean createIndex(String indexName);
    /*创建带有number_of_shards和number_of_replicas配置的索引*/
    boolean createIndex(Class clazz);
    /*创建索引*/
    boolean createIndexWithSettings(Class clazz);
    /*删除索引*/
    boolean deleteIndex(String indexName);
    /*删除type*/
    boolean deleteType(String indexName, String typeName);
    boolean deleteType(Class<?> clazz);
    /*判断索引是否存在*/
    boolean hasIndex(String indexName);
    /*查询*/
    <T> List<T> search(Class<T> clazz,String params);
    /*根据查询条件统计记录数*/
    long count(Class clazz, String params);
    /*根据查询条件删除数据*/
    boolean delete(Class clazz,String params);
    /*创建相关表对应的mapping*/
    boolean createMapping(Class clazz);
    /*判断相关表对应的mapping是否存在*/
    boolean hasMapping(Class clazz);
    boolean hasMapping(String indexName, String typeName);
}
