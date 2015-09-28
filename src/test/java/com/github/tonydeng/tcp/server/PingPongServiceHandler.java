package com.github.tonydeng.tcp.server;

import com.github.tonydeng.tcp.service.Ping;
import com.github.tonydeng.tcp.service.PingPongService;
import com.github.tonydeng.tcp.service.Pong;
import org.apache.commons.lang3.StringUtils;
import org.apache.thrift.TException;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

/**
 * Created by tonydeng on 15/9/28.
 */
@Service("pingPongService")
public class PingPongServiceHandler implements PingPongService.Iface {
    @Override
    public Pong knock(Ping ping) throws TException {

        String message  = ping.getMessage();
        String answer = StringUtils.reverse(message);

        Pong pong = new Pong();

        pong.setAnswer(answer);

        LoggerFactory.getLogger(PingPongServiceHandler.class).info("Got message {} and sent answer {}",message,answer);

        return pong;
    }
}
