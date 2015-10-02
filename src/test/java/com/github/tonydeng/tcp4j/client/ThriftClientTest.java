package com.github.tonydeng.tcp4j.client;

import com.github.tonydeng.tcp4j.ThriftClient;
import com.github.tonydeng.tcp4j.impl.ThriftClientImpl;
import com.github.tonydeng.tcp4j.pool.ThriftServerInfo;
import com.github.tonydeng.tcp4j.service.*;
import com.google.common.collect.Lists;
import org.apache.thrift.TException;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;
import java.util.function.Function;
import java.util.function.Supplier;

/**
 * Created by tonydeng on 15/9/28.
 */
@Ignore
public class ThriftClientTest {
    private static final Logger log = LoggerFactory.getLogger(ThriftClientTest.class);

//    private static final Supplier<List<ThriftServerInfo>> serverList = () -> Arrays.asList(
//            ThriftServerInfo.of("localhost", 9001)
//    );

    private static Supplier<List<ThriftServerInfo>> serverList;

    private static ThriftClient client ;

    @Before
    public void setup(){
        List<ThriftServerInfo> serverInfos = Lists.newArrayList(
                ThriftServerInfo.of("127.0.0.1", 9001)
        );
//        serverList = () -> serverInfos;

        client = new ThriftClientImpl((() -> serverInfos));
    }

    @Test
    public void testEcho() throws InterruptedException {
//        Supplier<List<ThriftServerInfo>> serverListProvider = () -> Arrays.asList( //
////                ThriftServerInfo.of("127.0.0.1", 9092), //
////                ThriftServerInfo.of("127.0.0.1", 9091), //
//                ThriftServerInfo.of("127.0.0.1", 9001));
//
//        // init pool client
//        ThriftClientImpl client = new ThriftClientImpl(serverListProvider);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 100; i++) {
            int counter = i;
            executorService.submit(() -> {
                try {
                    String result = client.iface(TestThriftService.Client.class).echo("hi " + counter + "!");
                    log.info("get result: {}", result);
                } catch (Throwable e) {
                    log.error("get client fail", e);
                }
            });
        }

        executorService.shutdown();
        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    //    @Test
    public void testTransport() throws TException {
        Function<TTransport, TProtocol> protocolProvider = transport -> {
            TCompactProtocol compactProtocol = new TCompactProtocol(transport);
            TMultiplexedProtocol protocol = new TMultiplexedProtocol(compactProtocol, "pingPongService");
            return protocol;
        };

        ThriftClient client = new ThriftClientImpl(serverList);
        Ping ping = new Ping("hi!");
        Pong pong = client.iface(PingPongService.Client.class, protocolProvider, 0).knock(ping);
        log.info("ping message:'{}'  pong answer:'{}'", ping.getMessage(), pong.getAnswer());
    }

//    @Test
    public void testPingPong() throws TException {
        ThriftClient client = new ThriftClientImpl(serverList);
        Ping ping = new Ping("hi!");
        Pong pong = client.iface(PingPongService.Client.class, TBinaryProtocol::new,0).knock(ping);
        log.info("ping message:'{}'  pong answer:'{}'", ping.getMessage(), pong.getAnswer());
    }

//    @Test
    public void testClientPoolPing() throws InterruptedException {

        ThriftClient client = new ThriftClientImpl(serverList);

        ExecutorService executorService = Executors.newFixedThreadPool(10);

        for (int i = 0; i < 100; i++) {
            int counter = i;

            executorService.submit(() -> {
                try {
                    Ping ping = new Ping("hi " + counter + "!");
//                    Pong pong = client.iface(PingPongService.Client.class).knock(ping);
                    Pong pong = client.iface(PingPongService.Client.class).knock(ping);
                    log.info("ping message:'{}'  pong answer:'{}'", ping.getMessage(), pong.getAnswer());
                } catch (TException e) {
                    e.printStackTrace();
                }
            });
        }

        executorService.shutdown();

        executorService.awaitTermination(1, TimeUnit.MINUTES);
    }

    //    @Test
    public void testPing() throws TException {
        log.info("test ping start....");

        TTransport transport = new TSocket("localhost", 9001);

        TProtocol protocol = new TCompactProtocol(transport);

        PingPongService.Client client = new PingPongService.Client(new TMultiplexedProtocol(protocol, "pingPongService"));

        transport.open();
        Ping ping = new Ping("hello world!");
        Pong pong = client.knock(ping);

        log.info("ping:'{}'  pong:'{}'", ping, pong);
        transport.close();
    }

    //    @Test
    public void testMail() throws TException {
        log.info("test mail start....");
        TTransport transport = new TSocket("localhost", 9001);

        TProtocol protocol = new TCompactProtocol(transport);
//        TProtocol protocol = new TBinaryProtocol(transport);

        MailService.Client client = new MailService.Client(new TMultiplexedProtocol(protocol, "mailService"));

        List<Recipient> recipients = new ArrayList<>();
        recipients.add(new Recipient("tonydeng", "dengtao@cim120.com.cn", "15201669909"));

        transport.open();

        client.sendMails(recipients, "subject", "content");

        transport.close();
    }


}
