package org.cnfire.elasticsearch.data;

/**
 * Created by caesar.zhu on 15-9-23.
 */

/**
 * ES操作类型相关的枚举类
 * */
public enum ESOperateType {
    QUERY("_search"),//查询
    COUNT("_count"),//统计
    DELETE("_delete");//删除
    private String value;
    private ESOperateType(String value){
        this.value = value;
    }
    public String getValue(){
        return value;
    }
    public static void main(String[] args) {
        for(ESOperateType type : ESOperateType.values()){
            System.out.println(type + ":" + type.value);
        }
    }
}
