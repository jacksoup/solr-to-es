package org.cnfire.elasticsearch.common;

/**
 * Created by caesar.zhu on 15-9-14.
 */
public class ParamsParseException extends Exception{
    /*序列化ID*/
    private static final long serialVersionUID = 1L;

    public ParamsParseException() {
        super("参数解析异常，请检查你的查询语句！");
    }

    public ParamsParseException(String msg) {
        super(msg);
    }

    public ParamsParseException(String msg, Throwable cause) {
        super(msg, cause);
    }

    public ParamsParseException(Throwable cause) {
        super(cause);
    }
}
