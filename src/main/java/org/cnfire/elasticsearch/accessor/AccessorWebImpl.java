package org.cnfire.elasticsearch.accessor;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;

import org.cnfire.elasticsearch.common.Constant;
import org.cnfire.elasticsearch.data.ESOperateType;
import org.cnfire.elasticsearch.data.RequestMethodType;
import org.cnfire.elasticsearch.util.SearchUtil;
import org.cnfire.elasticsearch.util.WebUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by jack zhu on 15-8-25.
 * IAccessor的基于es restful web API(通过web方式进行操作)的实现类
 */
public class AccessorWebImpl implements IAccessor {
    private static Logger LOG = LoggerFactory.getLogger(AccessorWebImpl.class);

    @Override
    public <T> boolean add(T model) {
        return false;
    }
    @Override
    public <T> boolean add(List<T> models) {
        return false;
    }
    @Override
    public <T> T get(Class<T> t, String id) {
        return null;
    }
    @Override
    public boolean delete(String id, Class clazz) {
        return false;
    }
    @Override
    public boolean createIndex(String indexName) {
        String settings = SearchUtil.getSettings();
        String response = WebUtil.httpExecute(RequestMethodType.PUT, Constant.BASE_URL + indexName, settings);
        JSONObject response_json = JSON.parseObject(response);
        boolean bool = Boolean.parseBoolean(response_json.get("acknowledged").toString());
        if(bool){
            LOG.info("创建索引:" + indexName + "成功！");
        }else{
            LOG.warn("创建索引:" + indexName + "失败！");
        }
        return bool;
    }

    @Override
    public boolean createIndex(Class clazz) {
        return false;
    }

    @Override
    public boolean createIndexWithSettings(Class clazz) {
        return false;
    }

    @Override
    public boolean deleteIndex(String indexName) {
        return false;
    }

    @Override
    public boolean deleteType(String indexName, String typeName) {
        return false;
    }

    @Override
    public boolean hasIndex(String indexName) {
        return false;
    }

    @Override
    public <T> List<T> search(Class<T> clazz,String params) {
        return null;
    }



    public <T> List<T> search(String params, Class<T> clazz,int start,int rows) {
        String url = Constant.BASE_URL + ESOperateType.QUERY.getValue() + "?q=" + params;
        System.out.println(url);
        String json = WebUtil.getJsonByUrl(url);
        JSONObject object = JSON.parseObject(json);
        JSONObject hits_ = (JSONObject) object.get("hits");
//        System.out.println("hits_:" + hits_);
        long total = (Integer) hits_.get("total");//记录数量
        if (total < 1) return null;
//        System.out.println("Total:" + total);
        String hitsStr = hits_.get("hits").toString();
//        System.out.println(hitsStr);
        hitsStr = hitsStr.substring(1, hitsStr.length() - 1);
//        System.out.println(hitsStr);
        String[] strArr = hitsStr.split(",\\{");
        List<T> list = new ArrayList<T>();
        for (int i = 0; i < strArr.length; i++) {
            String str = "";
            if (i != 0) {
                str = "{" + strArr[i];
            } else {
                str = strArr[i];
            }
//            System.out.println(str);
            JSONObject content = JSON.parseObject(str);
            String source = content.get("_source").toString();
            Map<String, Object> map = (Map<String, Object>) JSON.parse(source);
//            System.out.println(ESUtil.MapToModel(map, clazz));
            list.add(SearchUtil.MapToModel(map, clazz));
        }
        return list;
    }

    @Override
    public long count(Class clazz, String params) {
        return 0;
    }
    @Override
    public boolean delete(Class clazz, String params) {
        return false;
    }
    @Override
    public boolean createMapping(Class clazz) {
        String indexName = SearchUtil.getIndexName(clazz);
        String typeName = SearchUtil.getTypeName((clazz));
        String param = SearchUtil.getMapping(clazz);
        String response = WebUtil.httpExecute(RequestMethodType.PUT, Constant.BASE_URL + indexName + "/_mapping/" + typeName, param);
        JSONObject response_json = JSON.parseObject(response);
        boolean bool = Boolean.parseBoolean(response_json.get("acknowledged").toString());
        if(bool){
            LOG.info("创建mapping：" + indexName + "/"  + typeName + "成功");
        }else{
            LOG.warn("创建mapping：" + indexName + "/"  + typeName +  "失败！");
        }
        return bool;
    }
    @Override
    public boolean hasMapping(Class clazz) {
        String indexName = SearchUtil.getIndexName(clazz);
        String typeName = SearchUtil.getTypeName((clazz));
        String response = WebUtil.httpExecute(RequestMethodType.GET, Constant.BASE_URL + indexName + "/_mapping/" + typeName);
        JSONObject response_json = JSON.parseObject(response);
        boolean bool = false;
        if(!response_json.isEmpty()){
            bool = true;
        }else{
            bool = false;
        }
        LOG.info(bool ? "mapping：" + indexName + "/" + typeName + "存在" : "mapping：" + indexName + "/" + typeName + "不存在！");
        return bool;
    }

    @Override
    public boolean hasMapping(String indexName, String typeName) {
        return false;
    }

    @Override
    public boolean deleteType(Class clazz) {
        String indexName = SearchUtil.getIndexName(clazz);
        String typeName = SearchUtil.getTypeName((clazz));
        String response = WebUtil.httpExecute(RequestMethodType.DELETE, Constant.BASE_URL + indexName + "/_mapping/" + typeName);
        JSONObject response_json = JSON.parseObject(response);
        boolean bool = Boolean.parseBoolean(response_json.get("acknowledged").toString());
        if(bool){
            LOG.info("mapping：" + indexName + "/"  + typeName + "删除成功");
        }else{
            LOG.warn("mapping：" + indexName + "/"  + typeName + "删除失败");
        }
        return bool;
    }

}
