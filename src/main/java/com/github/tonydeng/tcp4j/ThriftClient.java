package com.github.tonydeng.tcp4j;

import org.apache.thrift.TServiceClient;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TTransport;

import java.util.function.Function;

/**
 * Created by tonydeng on 15/9/28.
 */
public interface ThriftClient {
    /**
     *
     * @param ifaceClass
     * @param <X>
     * @return
     */
    public <X extends TServiceClient> X iface(Class<X> ifaceClass);

    /**
     *
     * @param ifaceClass
     * @param hash
     * @param <X>
     * @return
     */
    public <X extends TServiceClient> X iface(Class<X> ifaceClass, int hash);

//    public <X extends TServiceClient> X iface(Class<X> ifaceClass, TProtocol protocol, String serviceName);

    /**
     *
     * @param ifaceClass
     * @param protocolProvider
     * @param hash
     * @param <X>
     * @return
     */
    public <X extends TServiceClient> X iface(Class<X> ifaceClass, Function<TTransport,TProtocol> protocolProvider, int hash);
}
