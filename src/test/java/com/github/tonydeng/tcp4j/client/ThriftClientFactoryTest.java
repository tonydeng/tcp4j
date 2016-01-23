package com.github.tonydeng.tcp4j.client;

import com.github.tonydeng.tcp4j.ThriftClient;
import com.github.tonydeng.tcp4j.ThriftClientFactory;
import org.junit.Before;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Properties;

/**
 * Created by tonydeng on 16/1/23.
 */
public class ThriftClientFactoryTest {
    private static final Logger log = LoggerFactory.getLogger(ThriftClientFactoryTest.class);

    private static Properties config;
    private static ThriftClientFactory factory;
    @Before
    public void init(){
        config = new Properties();
        config.setProperty("bluebird.thrift.server.1","127.0.0.1:9001");
        config.setProperty("bluebird.thrift.server.2","127.0.0.1:9002");
        config.setProperty("bluebird.thrift.server.3","127.0.0.1:9003");

        log.info("thrift config :'{}'", config);

        factory = new ThriftClientFactory(config);
    }

    @Test
    public void testGetClient(){
        ThriftClient client = factory.getDefaultThriftClient();
        log.info("client info:'{}'",client);
    }
}
