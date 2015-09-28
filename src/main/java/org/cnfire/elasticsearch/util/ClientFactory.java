package org.cnfire.elasticsearch.util;
import org.cnfire.elasticsearch.common.Constant;
import org.elasticsearch.client.transport.TransportClient;
import org.elasticsearch.common.settings.ImmutableSettings;
import org.elasticsearch.common.settings.Settings;
import org.elasticsearch.common.transport.InetSocketTransportAddress;

/**
 * Created by caesar.zhu on 15-8-20.
 * ElasticSearch的Client工厂类
 * 目前，只有一个实现，即使用TransportClient对象
 */
public class ClientFactory {
    private static TransportClient client;

    public static TransportClient getClient(){
        if(client == null){
            Settings settings = ImmutableSettings.settingsBuilder()
                    .put("cluster.name", Constant.CLUSTER_NAME).build();
            client = new TransportClient(settings)
                    .addTransportAddress(new InetSocketTransportAddress(Constant.HOST, Constant.CLIENT_PORT));
        }
        return client;
    }

}
