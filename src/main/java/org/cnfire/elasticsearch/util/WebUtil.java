package org.cnfire.elasticsearch.util;

import org.cnfire.elasticsearch.data.RequestMethodType;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.BufferedReader;
import java.io.DataOutputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * Created by caesar.zhu on 15-8-25.
 */
public class WebUtil {
    private static Logger LOG = LoggerFactory.getLogger(WebUtil.class);

    public static String httpExecute(RequestMethodType type,String url) {
        return httpExecute(type, url,null);
    }

    /**
     * HTTP操作工具
     * @param type ： HTTP操作类型，如GET、PUT、POST、DELETE、UPDATE
     * @param url ： 要访问的http url
     * @param params : 相关参数
     * @return
     */
    public static String httpExecute(RequestMethodType type ,String url,String params) {
//        LOG.info("访问web接口，地址：" + url_ + "," + "类型：" + type + ",参数：" + (params == null ? "无" : params));
        HttpURLConnection connection = null;
        try {
            URL URL = new URL(url);    // 把字符串转换为URL请求地址
            connection = (HttpURLConnection) URL.openConnection();// 打开连接
            connection.setRequestProperty("Content-Type", "application/x-www-form-urlencoded");
            connection.setRequestMethod(type.name());
            // Post 请求不能使用缓存
            connection.setUseCaches(false);
            connection.setDoInput(true);
            connection.setDoOutput(true);
            //获取输出流并写入参数
            if(params != null){
                DataOutputStream dataOutputStream = new DataOutputStream(connection.getOutputStream());
                dataOutputStream.write(params.getBytes());
                dataOutputStream.flush();
                dataOutputStream.close();
            }
            if(connection.getResponseCode()!=200){
                return "{\"acknowledged\":false}";
            }
            // 获取输入流
            BufferedReader br = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            String line;
            StringBuilder sb = new StringBuilder();
            while ((line = br.readLine()) != null) {// 循环读取流
                sb.append(line);
            }
            br.close();// 关闭流
//            LOG.info("http结果字符串为：" + sb.toString());
            return sb.toString();
        } catch (Exception e) {
            e.printStackTrace();
            LOG.error("http操作失败！");
            return "{\"acknowledged\":false}";
        }finally {
            connection.disconnect();// 断开连接
        }
    }

    /*根据url获取对应的json字符串<通过GET方式>(暂时不用，一切与HTTP相关的操都可以通过上面的httpExecute实现)*/
    public static String getJsonByUrl(String url_){
        String json = null;
        try{
            StringBuffer sb = new StringBuffer();
            URL url = new URL(url_);
            InputStreamReader isr = new InputStreamReader(url.openStream(),"UTF-8");
            char[] buffer = new char[1024];
            int len = 0;
            while((len=isr.read(buffer))!=-1){
                sb.append(buffer,0,len);
            }
            json = sb.toString();
        }catch (Exception e){
            e.printStackTrace();
        }
        return json;
    }

    public static void main(String[] args) {
        String params = "{\"settings\":{\"analysis\":{\"analyzer\":{\"douhao\":{\"pattern\":\",\",\"type\":\"pattern\"},\"fenhao\":{\"pattern\":\";\",\"type\":\"pattern\"},\"ik\":{\"alias\":[\"ik_analyzer\"],\"type\":\"org.elasticsearch.index.analysis.IkAnalyzerProvider\"},\"pinyin\":{\"pattern\":\"\\\\w\",\"type\":\"pattern\"}}},\"number_of_replicas\":\"1\",\"number_of_shards\":\"3\"}}\n";
        String url = "http://localhost:9200/news";
        System.out.println(httpExecute(RequestMethodType.PUT,url,params));
    }
}
