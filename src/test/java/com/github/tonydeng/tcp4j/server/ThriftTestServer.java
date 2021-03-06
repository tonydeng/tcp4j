package com.github.tonydeng.tcp4j.server;

import com.github.tonydeng.tcp4j.service.PingPongService;
import org.apache.thrift.TException;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TSimpleServer;
import org.apache.thrift.server.TThreadPoolServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TServerSocket;
import org.apache.thrift.transport.TServerTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.annotation.Resource;

/**
 * Created by tonydeng on 15/9/28.
 */
@Ignore
@Configuration
@ComponentScan(basePackages = "com.github.tonydeng.tcp4j.server")
public class ThriftTestServer {
    private static final Logger log = LoggerFactory.getLogger(ThriftTestServer.class);
    private static final int port=9001;
    @Resource
    private PingPongService.Iface pingPongService;


    @Test
    public void echoStart(){
        try {
            TServerTransport serverTransport = new TServerSocket(port);

            TThreadPoolServer.Args processor = new TThreadPoolServer.Args(serverTransport)
//                    .inputTransportFactory(new TFramedTransport.Factory())
//                    .outputTransportFactory(new TFramedTransport.Factory())
                    .protocolFactory(new TCompactProtocol.Factory())
                    .processor(new PingPongService.Processor<>(new PingPongServiceHandler()));
            //            processor.maxWorkerThreads = 20;
            TThreadPoolServer server = new TThreadPoolServer(processor);

            log.info("Starting the echo server...");
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

//    @Test
    public void start(){
        log.info("start thrift test server.......");
        try {

            TMultiplexedProcessor processor =  new TMultiplexedProcessor();

            processor.registerProcessor("pingPongService",new PingPongService.Processor<>(pingPongService));

            TCompactProtocol.Factory protocolFactory = new TCompactProtocol.Factory();

            TServerSocket socket = new TServerSocket(port);
            TServer server = new TThreadPoolServer(new TThreadPoolServer.Args(socket)
                    .processor(processor)
                    .protocolFactory(protocolFactory));

            server.serve();
        } catch (TException e) {
            e.printStackTrace();
        }

    }

//    @Test
    public void testThreadServerStart(){
        try {
            TServerTransport serverTransport =  serverTransport = new TServerSocket(port);

            TThreadPoolServer.Args processor = new TThreadPoolServer.Args(serverTransport)
                    .inputTransportFactory(new TFramedTransport.Factory())
                    .outputTransportFactory(new TFramedTransport.Factory())
                    .protocolFactory(new TCompactProtocol.Factory())
                    .processor(new PingPongService.Processor<>(pingPongService));
            //            processor.maxWorkerThreads = 20;
            TThreadPoolServer server = new TThreadPoolServer(processor);

            log.info("Starting the server...");
            server.serve();
        } catch (TTransportException e) {
            e.printStackTrace();
        }
    }

//    @Test
    public void testCompactServerStart(){
        try {
            TServerTransport serverTransport = new TServerSocket(port);

//            TThreadPoolServer.Args processor = new TThreadPoolServer.Args(serverTransport)
//                    .inputTransportFactory(new TFramedTransport.Factory())
//                    .outputTransportFactory(new TFramedTransport.Factory())
//                    .protocolFactory(new TCompactProtocol.Factory())
//                    .processor(new PingPongService.Processor<>(pingPongService));

//            TThreadPoolServer server = new TThreadPoolServer(processor);

            System.out.println("Starting the server...");
//            server.serve();

            TServer server = new TSimpleServer(new TSimpleServer.Args(serverTransport));
            server.serve();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
