package com.github.tonydeng.tcp.utils;

import org.junit.Ignore;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Set;

/**
 * Created by tonydeng on 15/9/28.
 */
@Ignore
public class ThriftClientUtilsTest {
    private static final Logger log = LoggerFactory.getLogger(ThriftClientUtilsTest.class);

    @Test
    public void testGetInterfaceMethodNames(){
        Set<String> methodNames = ThriftClientUtils.getInterfaceMethodNames(String.class);

        methodNames.stream().forEach(
                name -> log.info("class:{} -> method name:{}",String.class.getSimpleName(),name)
        );
    }

    @Test
    public void testRandomNextInt(){
        for(int i=0;i<20;i++){
            log.info("next int {}",ThriftClientUtils.randomNextInt());
        }
    }
}
