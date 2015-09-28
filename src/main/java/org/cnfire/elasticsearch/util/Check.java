package org.cnfire.elasticsearch.util;

import org.cnfire.elasticsearch.accessor.AccessorClientImpl;
import org.cnfire.elasticsearch.common.Constant;
import org.cnfire.elasticsearch.accessor.IAccessor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashSet;
import java.util.Set;

/**
 * Created by caesar.zhu on 15-8-24.
 * 系统的初始化工具类，包括重建和检测相关索引及映射
 */
public class Check {
    private static Logger LOG = LoggerFactory.getLogger(Check.class);
    IAccessor accessor = new AccessorClientImpl();
    /**
     * 重生计划，功能犹如“Rebirth”这两个字
     * 清空ES中的一切(删除索引，意味着什么都没了)，然后扫描实体类表根据表的元数据描述重新创建索引和mapping
     */
    public void rebirthPlan(){
        LOG.warn("=============================== 系统初始化 =====================================");
        Set<Class<?>> classes = ClassUtil.scanPackage(Constant.MODELS_PACKAGE);
        Set<String> indices = new HashSet<String>();
        for (Class<?> clazz : classes) {
            String indexName = SearchUtil.getIndexName(clazz);
            if(!SearchUtil.isDocument(clazz)){//如果该类不是ES实体类则跳过
                continue;
            }
           // 避免重复检测index是否存在
            if(!indices.contains(indexName)){
                if(accessor.hasIndex(indexName)) {//如果索引存在
                    accessor.deleteIndex(indexName);//删除索引
                    accessor.createIndexWithSettings(clazz);//创建索引
                    indices.add(indexName);
                }else{
                    accessor.createIndexWithSettings(clazz);//创建索引
                    indices.add(indexName);
                }
            }
            accessor.createMapping(clazz);//创建mapping
        }
    }

    /***
     *扫描实体类表根据表,然后创建相应的index和mapping  （如果没有则新建）
     */
    public void check() {
        LOG.info("=============================== 系统检测 =====================================");
        Set<Class<?>> classes = ClassUtil.scanPackage(Constant.MODELS_PACKAGE);
        Set<String> indices = new HashSet<String>();//加入set集合，用于避免重复检测index是否存在
        for (Class<?> clazz : classes) {
            String indexName = SearchUtil.getIndexName(clazz);
            if(indexName == null){//如果该类不是ES实体类则跳过
                continue;
            }
            if(!indices.contains(indexName)) {//如果该索引未进行检测
                if(!accessor.hasIndex(indexName)){//如果索引不存在
                    accessor.createIndexWithSettings(clazz);//创建索引
                    indices.add(indexName);
                }else{
                    indices.add(indexName);
                }
            }
            /*如果该mapping初始化检测属性init设置为false则跳过*/
            if (!SearchUtil.getInitValue(clazz)) continue;
            if(!accessor.hasMapping(clazz)){//如果mapping不存在
                accessor.createMapping(clazz);//创建mapping
            }
        }
    }

}
