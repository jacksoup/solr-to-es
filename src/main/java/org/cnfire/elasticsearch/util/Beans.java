package org.cnfire.elasticsearch.util;

import org.cnfire.elasticsearch.accessor.AccessorClientImpl;
import org.cnfire.elasticsearch.accessor.IAccessor;
import org.cnfire.elasticsearch.common.Init;

/**
 * Created by jack.zhu on 15-9-22.
 */
public class Beans {
    static { new Init(); }//调用初始化类

    private static IAccessor accessor = new AccessorClientImpl();

    public static IAccessor getAccessor(){
        return accessor;
    }
}
