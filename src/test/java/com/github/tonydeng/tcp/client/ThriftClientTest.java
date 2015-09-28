package com.github.tonydeng.tcp.client;

import com.github.tonydeng.tcp.ThriftClient;
import com.github.tonydeng.tcp.impl.ThriftClientImpl;
import com.github.tonydeng.tcp.pool.ThriftServerInfo;
import com.github.tonydeng.tcp.service.Ping;
import com.github.tonydeng.tcp.service.PingPongService;
import com.github.tonydeng.tcp.service.Pong;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.apache.thrift.transport.TTransportException;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Supplier;

/**
 * Created by tonydeng on 15/9/28.
 */
public class ThriftClientTest {
    private static  final Logger log = LoggerFactory.getLogger(ThriftClientTest.class);

//    @Test
    public void testClientPoolPing() throws InterruptedException {
        Supplier<List<ThriftServerInfo>> serverList = () -> Arrays.asList(
                ThriftServerInfo.of("localhost", 9001)
        );

        ThriftClient client = new ThriftClientImpl(serverList);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for(int i=0;i<100;i++){
            int counter = i;

            executorService.submit(() ->{
                try {
                    Ping ping = new Ping("hi "+ counter + "!");
                    Pong pong = client.iface(PingPongService.Client.class).knock(ping);
                    log.info("ping message:'{}'  pong answer:'{}'",ping.getMessage(),pong.getAnswer());
                } catch (TException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();

        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    @Test
    public void testPing() throws TException {
        log.info("test ping start....");

        TTransport transport = new TSocket("localhost",9001);

        TProtocol protocol = new TCompactProtocol(transport);
//        TProtocol protocol = new TBinaryProtocol(transport);

        PingPongService.Client client = new PingPongService.Client(new TMultiplexedProtocol(protocol,"pingPongService"));

        transport.open();

        Pong pong = client.knock(new Ping("Hello World!"));

        transport.close();
    }
}
