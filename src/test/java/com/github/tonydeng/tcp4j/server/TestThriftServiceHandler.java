package com.github.tonydeng.tcp4j.server;

import com.github.tonydeng.tcp4j.service.TestThriftService;
import org.apache.thrift.TException;
import org.springframework.stereotype.Service;

/**
 * Created by tonydeng on 15/9/28.
 */
@Service("testThriftService")
public class TestThriftServiceHandler implements TestThriftService.Iface {
    @Override
    public String echo(String message) throws TException {
        return message;
    }
}
