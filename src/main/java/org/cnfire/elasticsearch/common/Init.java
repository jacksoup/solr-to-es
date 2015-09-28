package org.cnfire.elasticsearch.common;

import com.google.common.base.Preconditions;
import org.cnfire.elasticsearch.util.Check;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Properties;

/**
 * Created by caesar.zhu on 15-9-22.
 */

/*初始化主类，执行相关初始化操作*/
public class Init {
    static{
        initConstants();
        excuteCheck();
        //do other

    }

    //执行初始化检测
    private static void excuteCheck(){
        if(Constant.INIT){
            new Check().rebirthPlan();
        }
        if(!Constant.INIT && Constant.IS_CHECK){
            new Check().check();
        }
    }

    /*初始化相关常量参数*/
    private static void initConstants(){
        InputStream in = ClassLoader.getSystemResourceAsStream("elasticsearch.properties");
        Properties prop = new Properties();
        try {
            prop.load(in);
        } catch (IOException e) {
            e.printStackTrace();
        }
        Constant.INIT = Boolean.parseBoolean(prop.getProperty("init", "false"));
        Constant.IS_CHECK = Boolean.parseBoolean(prop.getProperty("isCheck","true"));
        Constant.CLUSTER_NAME = prop.getProperty("cluster.name").trim();
        Constant.START = Integer.parseInt(prop.getProperty("start","0"));
        Constant.ROWS = Integer.parseInt(prop.getProperty("rows","10"));
        Constant.CLIENT_PORT = Integer.parseInt(prop.getProperty("client.port","9300").trim());
        Constant.WEB_PORT = Integer.parseInt(prop.getProperty("web.port","9200").trim());
        Constant.HOSTS = Arrays.asList(prop.getProperty("hosts","localhost").trim().split(","));
        Constant.MODELS_PACKAGE = prop.getProperty("models.package.dir").trim();
        Constant.HIGHLIGHT_PRE_TAGS = prop.getProperty("highlight.pre.tags","<span style=\"color:red\"").trim();
        Constant.HIGHLIGHT_POST_TAGS = prop.getProperty("highlight.post.tags","</span>").trim();
        Constant.BASE_URL = "http://" + Constant.HOSTS.get(0) + ":" + Constant.WEB_PORT + "/";

        Preconditions.checkNotNull(Constant.MODELS_PACKAGE, "请设置实体类所在的包路径（models.package）！");

//        System.out.println(Constant.IS_REDO);
//        System.out.println(Constant.IS_CHECK);
//        System.out.println(Constant.CLUST_NAME);
//        System.out.println(Constant.START);
//        System.out.println(Constant.ROWS);
//        System.out.println(Constant.CLIENT_PORT);
//        System.out.println(Constant.WEB_PORT);
//        System.out.println(Constant.HOST);
//        System.out.println(Constant.MODELS_PACKAGE);
//        System.out.println(Constant.HIGHLIGHT_PRE_TAGS);
//        System.out.println(Constant.HIGHLIGHT_POST_TAGS);
//        System.out.println(Constant.BASE_URL);

    }
}
