package com.github.tonydeng.tcp.impl;

import com.github.tonydeng.tcp.ThriftClient;
import com.github.tonydeng.tcp.pool.ThriftConnectionPoolProvider;
import com.github.tonydeng.tcp.pool.ThriftServerInfo;
import com.github.tonydeng.tcp.pool.impl.DefaultThriftConnectionPoolImpl;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.List;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by tonydeng on 15/9/28.
 */
public class ThriftClientImpl implements ThriftClient {
    private static final Logger log = LoggerFactory.getLogger(ThriftClientImpl.class);

    private final ThriftConnectionPoolProvider poolProvider;

    private final Supplier<List<ThriftServerInfo>> serverInfoProvider;

    /**
     *  构造ThriftClient实现.
     *
     * @param serverInfoProvider provide service list
     */
    public ThriftClientImpl(Supplier<List<ThriftServerInfo>> serverInfoProvider) {
        this(serverInfoProvider, DefaultThriftConnectionPoolImpl.getInstance());
    }

    /**
     * 构造ThriftClient实现.
     *
     * @param serverInfoProvider provide service list
     * @param poolProvider provide a pool
     */
    public ThriftClientImpl(Supplier<List<ThriftServerInfo>> serverInfoProvider,
                            ThriftConnectionPoolProvider poolProvider) {
        this.poolProvider = poolProvider;
        this.serverInfoProvider = serverInfoProvider;
    }


    @Override
    public <X extends TServiceClient> X iface(Class<X> ifaceClass) {
        return iface(ifaceClass);
    }

    @Override
    public <X extends TServiceClient> X iface(Class<X> ifaceClass, int hash) {
        return null;
    }

    @Override
    public <X extends TServiceClient> X iface(Class<X> ifaceClass, Function<TTransport, TProtocol> protocolProvider, int hash) {
        return null;
    }
}
