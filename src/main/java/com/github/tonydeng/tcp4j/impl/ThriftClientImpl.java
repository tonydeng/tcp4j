package com.github.tonydeng.tcp4j.impl;

import com.github.tonydeng.tcp4j.ThriftClient;
import com.github.tonydeng.tcp4j.exception.NoBackendException;
import com.github.tonydeng.tcp4j.pool.ThriftConnectionPoolProvider;
import com.github.tonydeng.tcp4j.pool.ThriftServerInfo;
import com.github.tonydeng.tcp4j.pool.impl.DefaultThriftConnectionPoolImpl;
import com.github.tonydeng.tcp4j.utils.ThriftClientUtils;
import javassist.util.proxy.Proxy;
import javassist.util.proxy.ProxyFactory;
import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.InvocationTargetException;
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
     * 构造ThriftClient实现
     *
     * @param serverInfoProvider
     */
    public ThriftClientImpl(List<ThriftServerInfo> serverInfoProvider) {
        this((() -> serverInfoProvider));
    }

    /**
     * 构造ThriftClient实现.
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
     * @param poolProvider       provide a pool
     */
    public ThriftClientImpl(Supplier<List<ThriftServerInfo>> serverInfoProvider,
                            ThriftConnectionPoolProvider poolProvider) {
        this.poolProvider = poolProvider;
        this.serverInfoProvider = serverInfoProvider;
    }


    @Override
    public <X extends TServiceClient> X iface(Class<X> ifaceClass) {
        return iface(ifaceClass, ThriftClientUtils.randomNextInt());
    }

    @Override
    public <X extends TServiceClient> X iface(Class<X> ifaceClass, int hash) {
        return iface(ifaceClass, TCompactProtocol::new, hash);
    }

//    @Override
//    public <X extends TServiceClient> X iface(Class<X> ifaceClass, TTransport transport, String serviceName){
//       TMultiplexedProtocol protocol =  new TMultiplexedProtocol(new TCompactProtocol(), "serviceName");
//        Function<TTransport, TProtocol> protocolProvider = new Function<TTransport, TProtocol>() {
//            @Override
//            public TProtocol apply(TTransport transport) {
//                return null;
//            }
//        }
//        return iface(ifaceClass,TMultiplexedProtocol::new,ThriftClientUtils.randomNextInt());
//        return null;
//    }

    @Override
    public <X extends TServiceClient> X iface(Class<X> ifaceClass, Function<TTransport, TProtocol> protocolProvider, int hash) {
        List<ThriftServerInfo> servers = serverInfoProvider.get();
        if (servers == null && servers.isEmpty()) {
            throw new NoBackendException();
        }

        hash = Math.abs(hash);

        hash = hash < 0 ? 0 : hash;

        ThriftServerInfo selected = servers.get(hash % servers.size());

        if (log.isTraceEnabled())
            log.trace("get connection for [{}] -> {} with hash:{}", ifaceClass, selected, hash);

        TTransport transport = poolProvider.getConnection(selected);

        TProtocol protocol = protocolProvider.apply(transport);

        ProxyFactory factory = new ProxyFactory();
        factory.setSuperclass(ifaceClass);

        factory.setFilter(
                m -> ThriftClientUtils.getInterfaceMethodNames(ifaceClass).contains(m.getName())
        );


        try {
            X x = (X) factory.create(new Class[]{TProtocol.class},
                    new Object[]{protocol});
            ((Proxy) x).setHandler((self, thisMethod, proceed, args) -> {
                boolean success = false;
                try {
                    Object result = proceed.invoke(self, args);
                    success = true;
                    return result;
                } finally {
                    if (success) {
                        poolProvider.returnConnection(selected, transport);
                    } else {
                        poolProvider.returnBrokenConnection(selected, transport);
                    }
                }
            });
            return x;
        } catch (NoSuchMethodException | IllegalArgumentException | InstantiationException
                | IllegalAccessException | InvocationTargetException e) {
            throw new RuntimeException("fail to create proxy.", e);
        }
    }
}
