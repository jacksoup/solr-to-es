package org.cnfire.elasticsearch.util;

import com.alibaba.fastjson.JSONObject;
import com.google.common.base.Preconditions;
import org.cnfire.elasticsearch.annotations.Document;
import org.cnfire.elasticsearch.annotations.Field;
import org.cnfire.elasticsearch.annotations.ID;
import org.cnfire.elasticsearch.common.Constant;
import org.elasticsearch.search.sort.SortOrder;
import org.cnfire.elasticsearch.common.ParamsParseException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.annotation.Annotation;
import java.lang.reflect.Method;
import java.util.*;

/**
 * Created by caesar.zhu on 15-8-21.
 * ElasticSearch相关的工具类，主要通过反射技术进行相关操作并获取相关类信息。
 * 功能包括：
 * 获取实体类的索引名称、获取实体类的类型名称、将ES实体类对象转换成Json对象、将Map对象转换为ES实体类对象、
 * 扫描指定包下面的ES实体类、获取建立索引所需的Setting信息、获取指定实体类相对应的mapping信息、
 * 以及解析es的查询参数
 */
public class SearchUtil {
    private static Logger LOG = LoggerFactory.getLogger(SearchUtil.class);

    /*获取指定实体类的索引名称*/
    public static <T>String getIndexName(T model){
        return getIndexName(model.getClass());
    }

    /*获取指定实体类的索引名称*/
    public static String getIndexName(Class clazz){
        Annotation[] annotationArr = clazz.getAnnotations();
        String indexName = null;
        for (Annotation annotation : annotationArr){
            if (annotation instanceof Document) {
                indexName = ((Document) annotation).index();
                break;
            }
        }
        return indexName;
    }

    /*获取指定实体类的类型名称*/
    public static <T>String getTypeName(T model){
        return getTypeName(model.getClass());
    }

    /*获取指定实体类的类型名称*/
    public static String getTypeName(Class clazz){
        Annotation[] annotationArr = clazz.getAnnotations();
        String typeName = null;
        for (Annotation annotation : annotationArr){
            if (annotation instanceof Document) {
                typeName = ((Document) annotation).type();
                break;
            }
        }
        return typeName;
    }

    /*判别一个类是否是Document（elasticsearch实体类）*/
    public static boolean isDocument(Class clazz) {
        return getIndexName(clazz) == null ? false : true;
    }

    /*获取指定实体类所在索引的number_of_shards值*/
    public static short getShards(Class clazz){
        Annotation[] annotationArr = clazz.getAnnotations();
        short number_of_shards = 0;
        for (Annotation annotation : annotationArr){
            if (annotation instanceof Document) {
                number_of_shards = ((Document) annotation).shards();
                break;
            }
        }
        return number_of_shards;
    }

    /*获取指定实体类所在索引的number_of_replicas值*/
    public static short getReplicas(Class clazz){
        Annotation[] annotationArr = clazz.getAnnotations();
        short number_of_replicas = 0;
        for (Annotation annotation : annotationArr){
            if (annotation instanceof Document) {
                number_of_replicas = ((Document) annotation).replicas();
                break;
            }
        }
        return number_of_replicas;
    }

    /*获取注解ID标志的字段名称*/
    public static String getidName(Class clazz){
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
        String id = null;
        outer: for (java.lang.reflect.Field field : fields) {
            Annotation[] ans = field.getAnnotations();
            for (Annotation a : ans) {
                if (a instanceof ID) {
                    id = field.getName();
                    break outer;
                }
            }
        }
        return id;
    }

    /*获取注解ID标志的字段对应的值*/
    public static <T> Object getidValue(T model){
        Class clazz = model.getClass();
        String idName = getidName(clazz);
        Object idValue = null;
        String firstLetter = idName.substring(0, 1).toUpperCase();
        String methodName = "get" + firstLetter + idName.substring(1);
        Method method = null;
        try {
            method = clazz.getMethod(methodName);
            idValue = method.invoke(model);
        }catch (Exception e) {
            e.printStackTrace();
        }
        return idValue;
    }

    /*将实体类对象转换为Json字符串*/
    public static <T>String ModelToJson(T model){
        String json = "{";
        Class clzz = model.getClass();
        java.lang.reflect.Field[] fields = clzz.getDeclaredFields();
        for (java.lang.reflect.Field field : fields){
            String fieldName = field.getName();
            Annotation[] ans = field.getAnnotations();
            for (Annotation a : ans){
                if (a instanceof Field) {
                    String firstLetter=fieldName.substring(0, 1).toUpperCase();
                    String getMethodName="get"+firstLetter+fieldName.substring(1);
                    //调用原对象的getXXX()方法
                    Method method;
                    Object value = null;
                    try {
                        method = clzz.getDeclaredMethod(getMethodName);
                        value = method.invoke(model);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (value == null){
                        value = "";
                    }
                    json += '\"' + fieldName + '\"' + ":" + '\"' + value + '\"' + ",";
                    continue;
                }
            }
        }
        json = json.substring(0,json.length()-1);
        json += "}";
        return json;
    }

    /**
     * 将传入的value字符串对象转换为type对应的具体子类类型对象（返回时会向上造型为Object对象）
     * 注：只接受String及基本类型及对应包装类的转换
     * */
    private static Object convert(Class type, String value){
        if(type.equals(String.class)){
            return value;
        }
        if(type.equals(int.class) || type.equals(Integer.class)){
            return Integer.parseInt(value);
        }
        if (type.equals(Short.class) || type.equals(short.class)) {
            if(value==null){
                return 0;
            }
            return Short.parseShort(value);
        }
        if (type.equals(Integer.class) || type.equals(int.class)) {
            if (value == null) {
                return 0;
            }
            return Integer.parseInt(value);
        }
        if (type.equals(Float.class) || type.equals(float.class)) {
            if (value == null) {
                return 0f;
            }
            return Float.parseFloat(value);
        }
        if (type.equals(Double.class) || type.equals(double.class)) {
            if (value == null) {
                return 0.0;
            }
            return Double.parseDouble(value);
        }
        if (type.equals(Long.class) || type.equals(long.class)) {
            if (value == null) {
                return 0l;
            }
            return Long.parseLong(value);
        }
        if (type.equals(Boolean.class) || type.equals(boolean.class)) {
            if (value == null) {
                return false;
            }
            return Boolean.parseBoolean(value);
        }
        return null;

    }

    /*将map对象转换为对应的实体类对象*/
    public static <K,V,T> T MapToModel(Map<K,V> map,Class<T> clazz) {
        Method[] methods = clazz.getMethods();
//        Set<Method> methodsSet = new HashSet<Method>();
        Map<String,Method> methodsMap = new HashMap<String, Method>();
        for (Method method: methods){
            //添加所有Setter方法
            if(method.getName().startsWith("set") && method.getParameterTypes().length == 1){
//                methodsSet.add(method);
                methodsMap.put(method.getName(),method);
            }
        }
        if(map == null) return null;
        T model = null;
        try {
            model = clazz.newInstance();
        } catch (Exception e) {
            e.printStackTrace();
        }
        for(K key: map.keySet()){
//            System.out.println(key + "：" + map.get(key));
            String field = (String)key;
            //ES中通过client API获得到的结果map,所有的value都是String类型
            // （虽然是Object,但具体的子类类型仍未String,算作ES java client API的一个bug）
            Object value = map.get(key);
            //拼装Setter方法名
            String firstLetter = field.substring(0, 1).toUpperCase();
            String methodName = "set" + firstLetter + field.substring(1);
//            System.out.println(methodName);
            try {
//                Method method = clazz.getDeclaredMethod(methodName,String.class);
//                method.invoke(model,value);
                Method method = methodsMap.get(methodName);
                if(method == null){
                    throw new Exception("没有" + method.getName() + "方法，请检查相应实体类(可能的问题：ES中新增了字段，但在实体类中没有定义)！");
                }
                Class parameterType = method.getParameterTypes()[0];//获取Setter的参数类型
                //根据参数类型对value(String类型)进行类型转换（转换为具体的子类类型）
                Object value2 = convert(parameterType, value.toString());
                //执行invoke方法，传入的参数类型必须匹配（第一个参数），同时传入的值value（第二个参数）具体的子类类型必须匹配
                //value可以进行向上造型操作，只要保证子类类型一致即可
                method.invoke(model,convert(parameterType,value2.toString()));
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return model;
    }

    /*获得指定实体类中的Type注解中的Init参数值*/
    public static boolean getInitValue(Class<?> modelClazz){
        boolean init = true;
        Annotation[] annotationArr = modelClazz.getAnnotations();
        for (Annotation annotation : annotationArr){
            if (annotation instanceof Document) {
                init = ((Document) annotation).init();
                break;
            }
        }
        return init;
    }

    /**
     * 调用parserParams()方法解析查询参数，并封装成map，供调用这查询，各key及对应说明如下：
         * searchParams ： 查询参数
         * filterParams ： 过滤参数
         * start ： 开始位置
         * rows ： 每页显示记录条数
         * showFields ： 要显示的字段
         * sortFields ： 排序字段及规则
         * isShowHighLight ： 是否高亮显示
         * HighLightFields ： 高亮显示的字段
         * HighLightPreTag ： 高亮显示的前置标记
         * HighLightPostTag ： 高亮显示的后置标记
     * @param params
     * @return Map<String,Object>
     * @throws ParamsParseException
     */
    public static Map<String,Object> getParamsMap(String params) {
        //调用parserParams()方法
        Map<String,Object> searchMap = null;
        try {
            searchMap = parserParams(params);
        } catch (ParamsParseException e) {
            e.printStackTrace();
        }
        String searchParams = searchMap.get("q").toString();//查询参数
        String filterParams = (String)searchMap.get("fq");//过滤参数
        int start = Integer.parseInt(searchMap.get("start").toString());//开始位置
        int rows = Integer.parseInt(searchMap.get("rows").toString());//每页显示记录条数
        List<String> showFields = (List<String>)searchMap.get("fl");//要显示的字段
        Map<String,SortOrder> sortFields = (HashMap<String,SortOrder>)searchMap.get("sort");//排序字段及规则
        boolean showHighLight = Boolean.parseBoolean((String)searchMap.get("hl"));//是否高亮显示
        List<String> HighLightFields = (List<String>)searchMap.get("hl.fl");//高亮显示的字段
        String HighLightPreTag = (String)searchMap.get("hl.simple.pre");//高亮显示的前置标记
        String HighLightPostTag = (String)searchMap.get("hl.simple.post");//高亮显示的后置标记

        Map<String,Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("searchParams",searchParams);
        paramsMap.put("filterParams",filterParams);
        paramsMap.put("start",start);
        paramsMap.put("rows",rows);
        paramsMap.put("showFields",showFields);
        paramsMap.put("sortFields",sortFields);
        paramsMap.put("showHighLight",showHighLight);
        paramsMap.put("HighLightFields",HighLightFields);
        paramsMap.put("HighLightPreTag",HighLightPreTag);
        paramsMap.put("HighLightPostTag",HighLightPostTag);
        return paramsMap;
    }

    /**
     *ES的查询参数解析工具
     * @param params 符合Solr查询语法的查询语句（为了兼容现有业务的查询接口以及方便查询，按照solr的查询语法格式做了一层包装，
     *               用户的查询语句只要符合solr的语法要求均可在此进行查询操作。）
     *        eg：q=name:'东方财富'&fq=tgsType:2&sort=name+desc,key+asc&start=10&rows=5&fl=name,age,sex,key&hl=true
     *            &hl.fl=name,logid&hl.simple.pre=<em>&hl.simple.post=</em>
     *        详情请参考solr的查询语法
     * @return
     */
    public static Map<String,Object> parserParams(String params) throws ParamsParseException {
//        String params = "q=name:hello&sort=name+desc,key+asc&start=10&rows=5&fl=name,age,sex,key" +
//                "&hl=true&hl.fl=name,logid&hl.simple.pre=<em>&hl.simple.post=</em>";
        if(params == null || params.length() == 0){
            throw new ParamsParseException();
        }
        Map<String,Object> map = new HashMap<String, Object>();
        String[] paramsArr = params.split("&");
        for (String str : paramsArr){
            String[] arr = str.split("=");
//            assert arr.length == 2;
            if(!(arr.length == 2)){
                if(arr.length == 1 && str.equals("")){
                    throw new ParamsParseException("查询参数解析异常,包含空子句:&空&！");
                }
                throw new ParamsParseException("查询参数解析异常，请保证以\"&\"分隔的每个查询子句" +
                        "中的字段和值之间用\"=\"连接,当前出错子句：" + str);
            }
            String param = str.split("=")[0];
            String value = str.split("=")[1];
            map.put(param,value);
        }
        Set<String> paramSet = map.keySet();
        if (!paramSet.contains("q")){
            throw new ParamsParseException("查询参数不正确，没有包含必要的查询字段(q字段)!");
        }
        if (!paramSet.contains("fq")){
            map.put("fq",null);
        }
        if (!paramSet.contains("start")){
            map.put("start", Constant.START);
        }else{
            int value = Integer.parseInt(map.get("start").toString());
            Preconditions.checkArgument(value >= 0, "查询参数解析异常，start参数不能小于0,当前值：%s，请检查你的参数！", value);
//            throw new ParamsParseException();
        }
        if (!paramSet.contains("rows")){
            map.put("rows", Constant.ROWS);
        }else{
            int value = Integer.parseInt(map.get("rows").toString());
            Preconditions.checkArgument(value >= 0, "查询参数解析异常，rows参数不能小于0,当前值：%s，请检查你的参数！", value);
        }
        if (!paramSet.contains("fl")){
            map.put("fl",null);
        }else{
            map.put("fl",Arrays.asList(map.get("fl").toString().split(",")));
        }
        if (!paramSet.contains("hl")){
            map.put("hl","false");
        }else{
            if(Boolean.parseBoolean(map.get("hl").toString()) == true){
                if (!paramSet.contains("hl.simple.pre")){
                    map.put("hl.simple.pre", Constant.HIGHLIGHT_PRE_TAGS);
                }
                if (!paramSet.contains("hl.simple.post")){
                    map.put("hl.simple.post", Constant.HIGHLIGHT_POST_TAGS);
                }
                if(!paramSet.contains("hl.fl")){
                    throw new ParamsParseException("已开启高亮查询，但是没有设置高亮查询字段！");
                }
                map.put("hl.fl",Arrays.asList(map.get("hl.fl").toString().split(",")));
            }else{
                map.put("hl.fl",null);
            }
        }
        if (!paramSet.contains("sort")){
            map.put("sort",null);
        }else{
            String sortStr = map.get("sort").toString();
            String[] srotArr = sortStr.split(",");
            Map<String,SortOrder> sortFields = new HashMap<String, SortOrder>();
            for(String str : srotArr){
                if(!(str.split("\\+").length == 2)){
                    throw new ParamsParseException("查询参数解析异常，排序参数设置语法错误,正确格式参考：字段+desc/asc！！");
                }
                String field = str.split("\\+")[0];
                String sort = str.split("\\+")[1];
                if(!("desc".equalsIgnoreCase(sort) || "asc".equalsIgnoreCase(sort))){
                    throw new ParamsParseException("查询参数解析异常，排序参数设置语法错误,请用asc/desc表示排序！");
                }
                sortFields.put(field,"desc".equalsIgnoreCase(sort) ? SortOrder.DESC : SortOrder.ASC);
            }
            map.put("sort",sortFields);
        }
        return map;
    }

    /*扫描包下面的所有实体类<暂时不用>*/
    private void scanModelsByPackage(){
        Set<Class<?>> classes = ClassUtil.scanPackage(Constant.MODELS_PACKAGE);
        for (Class<?> clazz : classes) {
            String indexName = SearchUtil.getIndexName(clazz);
            String typeName = SearchUtil.getTypeName(clazz);
//            LOG.info("发现实体类：" + clazz + ",对应的索引：" + indexName + ",对应的类型：" + typeName);
            java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
            String fieldsStr = "[";
            for (java.lang.reflect.Field field : fields) {
//                System.out.println(field.getName());
                Annotation[] ans = field.getAnnotations();
                //判断是否是ESField字段
                for (Annotation a : ans) {
                    if (a instanceof Field) {
                        JSONObject fieldJson = new JSONObject();
                        fieldsStr += field.getName() + ",";
                        break;
                    }
                }
            }
            fieldsStr = fieldsStr.substring(0,fieldsStr.length()-1) + "]";
            LOG.info("扫描实体类：" + clazz + ",对应的索引：" + indexName + ",对应的类型：" + typeName + ",对应的Field有效字段为：" + fieldsStr);
        }
    }

    /*获取指定类的mapping,并组装成json字符串*/
    public static String getMapping(Class clazz){
        if (!getInitValue(clazz)) return null;
        JSONObject rootJson = new JSONObject();
        JSONObject typeJson = new JSONObject();
        String indexName = SearchUtil.getIndexName(clazz);
        String typeName = SearchUtil.getTypeName(clazz);
        java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
        String fieldsStr = "[";
        JSONObject propertiesJson = new JSONObject();
        for (java.lang.reflect.Field field : fields) {
//                System.out.println(field.getName());
            Annotation[] ans = field.getAnnotations();
            //判断是否是ESField字段
            for (Annotation a : ans) {
                if (a instanceof Field) {
                    JSONObject fieldJson = new JSONObject();
                    fieldsStr += field.getName() + ",";
                    Field ann = (Field) a;
                    Object type = ann.type().name().toLowerCase();
                    String index = ann.index().name();
                    String stored = ann.stored() + "";
                    String analyzer = ann.analyzer();
                    //type = "string", index = "analyzed", stored = "true", analyzer = "standard"
                    if(type.equals("Auto")){//如果是Auto类型则跳过，不进行mapping设置
                        continue;
                    }
                    fieldJson.put("type",type);
                    fieldJson.put("index",index);
                    fieldJson.put("stored",stored);
                    fieldJson.put("analyzer",analyzer);
                    propertiesJson.put(field.getName(),fieldJson);
                    break;
                }
            }
        }
        typeJson.put("properties",propertiesJson);
        rootJson.put(typeName,typeJson);
        fieldsStr = fieldsStr.substring(0,fieldsStr.length()-1) + "]";
        LOG.info("扫描实体类：" + clazz + ",发现索引：" + indexName + ",发现类型：" + typeName + ",发现Field有效字段为：" + fieldsStr);
        LOG.info("扫描类" + clazz.getName() + "完成，生成的mapping如下：\n" + rootJson.toJSONString());
        return rootJson.toJSONString();
    }

    /*扫描包下面的所有实体类，并获取其对应的mapping,并组装成json字符串<暂时不用>*/
    public static String getMappingsByPackage(){
        Set<Class<?>> classes = ClassUtil.scanPackage(Constant.MODELS_PACKAGE);
        JSONObject rootJson = new JSONObject();
        for (Class<?> clazz : classes) {
            boolean init = true;
            Annotation[] annotationArr = clazz.getAnnotations();
            for (Annotation annotation : annotationArr){
                if (annotation instanceof Document) {
                    init = ((Document) annotation).init();
                    break;
                }
            }
            if (!init) continue;
            JSONObject typeJson = new JSONObject();
            String indexName = SearchUtil.getIndexName(clazz);
            String typeName = SearchUtil.getTypeName(clazz);
//            LOG.info("发现实体类：" + clazz + ",对应的索引：" + indexName + ",对应的类型：" + typeName);
            java.lang.reflect.Field[] fields = clazz.getDeclaredFields();
            String fieldsStr = "[";
            JSONObject propertiesJson = new JSONObject();
            for (java.lang.reflect.Field field : fields) {
//                System.out.println(field.getName());
                Annotation[] ans = field.getAnnotations();
                //判断是否是ESField字段
                for (Annotation a : ans) {
                    if (a instanceof Field) {
                        JSONObject fieldJson = new JSONObject();
                        fieldsStr += field.getName() + ",";
                        Field ann = (Field) a;
                        String type = ann.type().name();
                        String index = ann.index().name();
                        String stored = ann.stored()+"";
                        String analyzer = ann.analyzer();
                        //type = "string", index = "analyzed", stored = "true", analyzer = "standard"
                        fieldJson.put("type",type);
                        fieldJson.put("index",index);
                        fieldJson.put("stored",stored);
                        fieldJson.put("analyzer",analyzer);
                        propertiesJson.put(field.getName(),fieldJson);
                        break;
                    }
                }
            }
            typeJson.put("properties",propertiesJson);
            rootJson.put(typeName,typeJson);
            fieldsStr = fieldsStr.substring(0,fieldsStr.length()-1) + "]";
            LOG.info("扫描实体类：" + clazz + ",对应的索引：" + indexName + ",对应的类型：" + typeName + ",对应的ES有效字段为：" + fieldsStr);
        }
        LOG.info("扫描包" + Constant.MODELS_PACKAGE + "下面的实体类完成，生成的mapping如下：\n" + rootJson.toJSONString());
        return rootJson.toJSONString();
    }

    /*读取setting配置文件*/
    public static String getSettings(Class clazz){
        //获得实体类对应的index对应的settings配置文件
        Annotation[] annotationArr = clazz.getAnnotations();
        String settingsFile = null;
        for (Annotation annotation : annotationArr){
            if (annotation instanceof Document) {
                settingsFile = ((Document) annotation).settings();
                break;
            }
        }
        //是否使用指定配置文件来创建索引
        boolean crateIndexWithSettings = "".endsWith(settingsFile) ? false : true;
        if(!crateIndexWithSettings) {
            return null;
        }
        LOG.info("读取settings文件：classpath/" + settingsFile + " ....");
        InputStream stream = ClassLoader.getSystemResourceAsStream(settingsFile);
//        Preconditions.checkNotNull(stream,"settings.yml文件不存在");
        if(stream == null){
            throw new RuntimeException("classpath/" + settingsFile + "文件不存在");
        }
        BufferedReader br = new BufferedReader(new InputStreamReader(stream));
        String line;
        StringBuffer settings = new StringBuffer();
        try {
            while((line = br.readLine())!= null){//一次输出文本中的一行内容
//                System.out.println(line);
                if(!line.startsWith("#"))
                    settings.append(line.trim());
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
        return settings.toString();
    }

    /*生成指定的Setting信息，暂时不用*/
    public static String getSettings(){
        JSONObject root = new JSONObject();
        JSONObject settings = new JSONObject();
        settings.put("number_of_shards","3");
        settings.put("number_of_replicas","1");
        JSONObject analysis = new JSONObject();
        JSONObject analyzer = new JSONObject();
        JSONObject pinyin_analyzer = new JSONObject();
        pinyin_analyzer.put("type","pattern");
        pinyin_analyzer.put("pattern","\\w");
        pinyin_analyzer.put("alias", new String[]{"pinyin"});
        JSONObject douhao_analyzer = new JSONObject();
        douhao_analyzer.put("type","pattern");
        douhao_analyzer.put("pattern",",");
        douhao_analyzer.put("alias", new String[]{"douhao"});
        JSONObject fenhao_analyzer = new JSONObject();
        fenhao_analyzer.put("type","pattern");
        fenhao_analyzer.put("pattern",";");
        fenhao_analyzer.put("alias", new String[]{"fenhao"});
        JSONObject ik_analyzer = new JSONObject();
        ik_analyzer.put("type","org.elasticsearch.index.analysis.IkAnalyzerProvider");
        ik_analyzer.put("alias",new String[]{"ik_analyzer"});
        analyzer.put("pinyin_analyzer",pinyin_analyzer);
        analyzer.put("douhao_analyzer",douhao_analyzer);
        analyzer.put("fenhao_analyzer",fenhao_analyzer);
        analyzer.put("ik",ik_analyzer);
        analysis.put("analyzer",analyzer);
        settings.put("analysis",analysis);
        root.put("settings",settings);
        return root.toJSONString();
    }


    public static void main(String[] args) throws Exception {
//        MGlobal model = new MGlobal();
//        model.setId("-1653233969");
//        model.setLogicId("1000521938");
//        model.setName("恒指摩通六七牛Q");
//        model.setPinyin("HZMTLQNQ");
//        model.setTgsType("4");
//        model.setKey("4:1000521938");
//        model.setTypeCode("001006007");
//        model.setTypeName("牛熊证");
//        model.setUrl("EM://quoteloader?view=quotestockview&type=trend&****");
//        model.setOrderNumber("67587.HK");
//        model.setFileName("61");
//        model.setFuncType("006");
//        model.setSecuFullCode("secuFullCode");
//        model.setUrl1("");
//        String json = ModelToJson(model);
//        System.out.println(json);
//
//        System.out.println(getIndexNameByModel(MGlobal.class));
//        System.out.println(getTypeNameByModel(MGlobal.class));
        String params = "q=name:hello&sort=name+desc,key+asc&start=10&rows=5&fl=name,age,sex,key" +
                "&hl=true&hl.fl=name,logid&hl.simple.pre=<em>&hl.simple.post=</em>";

        System.out.println(parserParams(params));
    }

}
