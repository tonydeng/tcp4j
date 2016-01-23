package com.github.tonydeng.tcp4j.pool.factory;

import com.github.tonydeng.tcp4j.pool.ThriftServerInfo;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.function.Function;

/**
 * Created by tonydeng on 15/9/28.
 */
public final class ThriftConnectionFactory implements KeyedPooledObjectFactory<ThriftServerInfo, TTransport> {
    private static final Logger log = LoggerFactory.getLogger(ThriftConnectionFactory.class);
    private final Function<ThriftServerInfo, TTransport> transportProvider;

    public ThriftConnectionFactory(Function<ThriftServerInfo, TTransport> transportProvider) {
        this.transportProvider = transportProvider;
    }

    @Override
    public PooledObject<TTransport> makeObject(ThriftServerInfo thriftServerInfo) throws Exception {
        TTransport transport = transportProvider.apply(thriftServerInfo);
        transport.open();
        DefaultPooledObject<TTransport> result = new DefaultPooledObject<>(transport);
        if (log.isTraceEnabled())
            log.trace("make new thrith connections:{}", thriftServerInfo);
        return result;
    }

    @Override
    public void destroyObject(ThriftServerInfo thriftServerInfo, PooledObject<TTransport> pooledObject) throws Exception {
        TTransport transport = pooledObject.getObject();
        if (transport != null && transport.isOpen()) {
            transport.close();
            if (log.isTraceEnabled())
                log.trace("close thrift connection:{}", thriftServerInfo);
        }
    }

    @Override
    public boolean validateObject(ThriftServerInfo thriftServerInfo, PooledObject<TTransport> pooledObject) {
        try {
            return pooledObject.getObject().isOpen();
        } catch (Throwable e) {
            if(log.isErrorEnabled()){
                log.error("fail tovalidate tsocket:{}",thriftServerInfo,e);
            }
            return false;
        }
    }

    @Override
    public void activateObject(ThriftServerInfo thriftServerInfo, PooledObject<TTransport> pooledObject) throws Exception {

    }

    @Override
    public void passivateObject(ThriftServerInfo thriftServerInfo, PooledObject<TTransport> pooledObject) throws Exception {

    }
}
