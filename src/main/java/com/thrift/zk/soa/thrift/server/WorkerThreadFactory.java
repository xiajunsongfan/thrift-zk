package com.thrift.zk.soa.thrift.server;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicLong;

/**
 * Author: baichuan - xiajun
 * Date: 2016-08-13
 * Time: 18:07:00
 */
public class WorkerThreadFactory implements ThreadFactory {
    private final AtomicLong count=new AtomicLong(0);
    @Override
    public Thread newThread(Runnable r) {
        Thread t=new Thread(r);
        t.setDaemon(true);
        t.setName("thrift-worker-thread-"+count.incrementAndGet());
        return t;
    }
}
