package com.github.tonydeng.tcp4j;

import com.github.tonydeng.tcp4j.impl.ThriftClientImpl;
import com.github.tonydeng.tcp4j.pool.ThriftServerInfo;
import com.google.common.collect.Lists;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.Properties;

/**
 * Created by tonydeng on 16/1/23.
 */
public class ThriftClientFactory {
    private static final Logger log = LoggerFactory.getLogger(ThriftClientFactory.class);

    private static ThriftClient defaultClient;

    private static List<ThriftServerInfo> thriftServerInfos;

    public ThriftClientFactory(Properties thriftClientConfig) {

        if (thriftServerInfos == null) {
            thriftServerInfos = Lists.newArrayList();
            thriftClientConfig.stringPropertyNames().forEach(
                    name -> {
                        String[] values = thriftClientConfig.getProperty(name).split(":");
                        thriftServerInfos.add(ThriftServerInfo.of(values[0], Integer.valueOf(values[1])));
                    }
            );

            if(log.isDebugEnabled()){
                log.debug("init thrift servers info:'{}'",thriftServerInfos);
            }
        }
    }

    public synchronized ThriftClient getDefaultThriftClient() {
        if (defaultClient == null) {
            defaultClient = new ThriftClientImpl(() -> thriftServerInfos);
        }

        return defaultClient;
    }

}
