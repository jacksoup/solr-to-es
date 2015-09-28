package org.cnfire.elasticsearch.common;

/**
 * Created by jack zhu on 15-8-21.
 * ElasticSearch相关的常量类
 *
 * 由common.properties中的参数进行初始化
 */
public class Constant {
    //系统启动时是否进行初始化操作（删除已有的所有数据并重新生成相应的index与mapping）
    public static boolean INIT;
    //系统启动时是否进行初始化检测（包括检测index、mapping是否存在，如果不存在则根据实体类的注解描述自动解析并创建相应的index、mapping）
    public static boolean IS_CHECK;
    public static int START;//搜索默认开始位置
    public static int ROWS;//搜索结果条数
    public static String HOST;//服务器地址
    public static int CLIENT_PORT;//client端口号
    public static int WEB_PORT;//web端口号
    public static String BASE_URL;
    public static String CLUSTER_NAME;//es集群名称
    public static String MODELS_PACKAGE;//实体类所在包路径
    public static String HIGHLIGHT_PRE_TAGS;//默认的高亮搜索前缀
    public static String HIGHLIGHT_POST_TAGS;//默认的高亮搜索后缀

}
