package com.thrift.zk.soa.exception;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: xiajun
 * Date: 16/12/04 16:27
 * 全局线程异常捕捉器
 */
public class ThriftUncaughtExceptionHandler implements Thread.UncaughtExceptionHandler {
    private final static Logger LOGGER = LoggerFactory.getLogger(ThriftUncaughtExceptionHandler.class);

    public void uncaughtException(Thread t, Throwable e) {
        LOGGER.error("Thread : " + t.getName() + " exception.", e);
    }
}
