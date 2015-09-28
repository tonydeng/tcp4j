package com.github.tonydeng.tcp.pool;

import com.google.common.base.Splitter;
import com.google.common.collect.MapMaker;

import javax.swing.plaf.TableHeaderUI;
import java.util.List;
import java.util.concurrent.ConcurrentMap;

/**
 * Created by tonydeng on 15/9/28.
 */
public class ThriftServerInfo {

    private static ConcurrentMap<String, ThriftServerInfo> allInfos = new MapMaker().weakValues().makeMap();

    private static Splitter splitter = Splitter.on(":");

    private final String host;
    private final int port;

    /**
     * 分析主机和端口
     *
     * @param hostAndPort
     */
    private ThriftServerInfo(String hostAndPort) {
        List<String> split = splitter.splitToList(hostAndPort);

        assert split.size() == 2;

        this.host = split.get(0);
        this.port = Integer.valueOf(split.get(1));
    }

    /**
     * @param host
     * @param port
     * @return
     */
    public static final ThriftServerInfo of(String host, int port) {
        return allInfos.computeIfAbsent(host + ":" + port, ThriftServerInfo::new);
    }


    /**
     * @param hostAndPort
     * @return
     */
    public static final ThriftServerInfo of(String hostAndPort) {
        return allInfos.computeIfAbsent(hostAndPort, ThriftServerInfo::new);
    }

    public String getHost() {
        return host;
    }

    public int getPort() {
        return port;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (!(obj instanceof ThriftServerInfo)) {
            return false;
        }
        ThriftServerInfo other = (ThriftServerInfo) obj;
        if (host == null) {
            if (other.host == null)
                return false;
        }else  if(!host.equals(other.host)){
            return false;
        }
        if(port != other.port){
            return false;
        }
        return true;
    }

    @Override
    public int hashCode() {
        final int prime =31;
        int result = 1;
        result  = prime * result + ((host == null) ? 0 : host.hashCode());
        return result;
    }

    @Override
    public String toString() {
        return "ThriftServerInfo [host=" + host + ", port=" + port + "]";
    }
}
