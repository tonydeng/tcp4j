package com.github.tonydeng.tcp4j.pool.impl;

import com.github.tonydeng.tcp4j.factory.ThriftConnectionFactory;
import com.github.tonydeng.tcp4j.pool.ThriftConnectionPoolProvider;
import com.github.tonydeng.tcp4j.pool.ThriftServerInfo;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.concurrent.TimeUnit;
import java.util.function.Function;

/**
 * Created by tonydeng on 15/9/28.
 */
public class DefaultThriftConnectionPoolImpl implements ThriftConnectionPoolProvider {
    private static final Logger log = LoggerFactory.getLogger(DefaultThriftConnectionPoolImpl.class);

    private static final int MIN_CONN = 1;
    private static final int MAX_CONN = 1000;
    private static final int TIMEOUT = (int) TimeUnit.MINUTES.toMillis(5);

    private final GenericKeyedObjectPool<ThriftServerInfo, TTransport> connections;

    /**
     * 构造默认Thrift连接池实现
     *
     * @param config
     * @param transportProvider
     */
    public DefaultThriftConnectionPoolImpl(GenericKeyedObjectPoolConfig config,
                                           Function<ThriftServerInfo, TTransport> transportProvider) {
        connections = new GenericKeyedObjectPool<ThriftServerInfo, TTransport>(new ThriftConnectionFactory(transportProvider), config);
    }

    /**
     * 构造默认Thrift连接池实现
     *
     * @param config
     */
    public DefaultThriftConnectionPoolImpl(GenericKeyedObjectPoolConfig config) {
        this(config, info -> {
//            TSocket tSocket = new TSocket(info.getHost(), info.getPort());
//            tSocket.setTimeout(TIMEOUT);
//            TFramedTransport transport = new TFramedTransport(tSocket);
//            return transport;
            TSocket socket = new TSocket(info.getHost(),info.getPort());
            socket.setTimeout(TIMEOUT);
            return socket;
        });
    }

    /**
     * 懒构造类
     */
    private static class LazyHolder {
        private static final DefaultThriftConnectionPoolImpl INSTANCE;

        static {
            GenericKeyedObjectPoolConfig config = new GenericKeyedObjectPoolConfig();
            config.setMaxTotal(MAX_CONN);
            config.setMaxTotalPerKey(MAX_CONN);
            config.setMaxIdlePerKey(MAX_CONN);
            config.setMinIdlePerKey(MIN_CONN);
            config.setTestOnBorrow(true);
            config.setMinEvictableIdleTimeMillis(TimeUnit.MINUTES.toMillis(1));
            ;
            config.setSoftMinEvictableIdleTimeMillis(TimeUnit.MINUTES.toMillis(1));
            config.setJmxEnabled(false);

            INSTANCE = new DefaultThriftConnectionPoolImpl(config);
        }
    }

    public static final DefaultThriftConnectionPoolImpl getInstance() {
        return LazyHolder.INSTANCE;
    }

    @Override
    public TTransport getConnection(ThriftServerInfo thriftServerInfo) {
        try {
            return connections.borrowObject(thriftServerInfo);
        } catch (Exception e) {
            if (log.isErrorEnabled())
                log.error("fail to get connection for {} error:'{}'", thriftServerInfo, e);
            throw new RuntimeException(e);
        }
    }

    @Override
    public void returnConnection(ThriftServerInfo thriftServerInfo, TTransport transport) {
        connections.returnObject(thriftServerInfo, transport);
    }

    @Override
    public void returnBrokenConnection(ThriftServerInfo thriftServerInfo, TTransport transport) {
        try {
            connections.invalidateObject(thriftServerInfo, transport);
        } catch (Exception e) {
            if(log.isErrorEnabled()){
                log.error("fail to invalid object:{},{}",thriftServerInfo,e);
                throw new RuntimeException(e);
            }

        }
    }
}
