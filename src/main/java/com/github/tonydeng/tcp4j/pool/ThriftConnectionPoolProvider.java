package com.github.tonydeng.tcp4j.pool;

import org.apache.thrift.transport.TTransport;

/**
 * Created by tonydeng on 15/9/28.
 */
public interface ThriftConnectionPoolProvider {

    /**
     * 获取thrift连接
     *
     * @param thriftServerInfo
     * @return
     */
    public TTransport getConnection(ThriftServerInfo thriftServerInfo);

    /**
     * 释放返回连接
     * @param thriftServerInfo
     * @param transport
     */
    public void returnConnection(ThriftServerInfo thriftServerInfo, TTransport transport);

    /**
     * 释放返回失败的连接
     * @param thriftServerInfo
     * @param transport
     */
    public void returnBrokenConnection(ThriftServerInfo thriftServerInfo, TTransport transport);
}
