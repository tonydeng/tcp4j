package com.github.tonydeng.tcp.server;

import com.github.tonydeng.tcp.service.PingPongService;
import org.apache.thrift.TException;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TServerSocket;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Created by tonydeng on 15/9/28.
 */
@Configuration
@ComponentScan(basePackages = "com.github.tonydeng.tcp.server")
public class ThriftTestServer {
    private static final Logger log = LoggerFactory.getLogger(ThriftTestServer.class);

    @Resource
    private PingPongService.Iface pingPongService;

    @Test
    public void start(){
        log.info("start thrift test server.......");
        try {

            TMultiplexedProcessor processor =  new TMultiplexedProcessor();

            processor.registerProcessor("pingPongService",new PingPongService.Processor<>(pingPongService));

            TCompactProtocol.Factory protocolFactory = new TCompactProtocol.Factory();

            TServerSocket socket = new TServerSocket(9001);
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(socket)
                    .processor(processor)
                    .protocolFactory(protocolFactory));

            server.serve();
        } catch (TException e) {
            e.printStackTrace();
        }
    }
}
