
package com.thrift.zk.soa.thrift.client;

import com.thrift.zk.soa.exception.SoaException;
import com.thrift.zk.soa.pool.ClusterPool;
import net.sf.cglib.proxy.Enhancer;
import net.sf.cglib.proxy.MethodInterceptor;
import net.sf.cglib.proxy.MethodProxy;

import java.io.IOException;
import java.lang.reflect.Method;
import java.net.SocketException;
import java.net.SocketTimeoutException;

/**
 * Author: xiajun
 * Date: 2016-10-30
 * Time: 12:01:00
 * thrift 客户端代理类
 */
public final class ThriftClientProxy<T> implements MethodInterceptor {
    private final Enhancer enhancer = new Enhancer();
    private ClusterPool<ShardedThrift> pool;
    private Class clientClass;

    public ThriftClientProxy(ClusterPool<ShardedThrift> pool, Class clientClass) {
        this.pool = pool;
        this.clientClass = clientClass;
    }

    /**
     * 获取代理类
     *
     * @param interfaceClass thrift 客户端接口对象
     * @param type           构造函数中的参数类型集合
     * @param args           构造函数中的参数
     * @return
     */
    public T getProxy(Class<T> interfaceClass, Class[] type, Object[] args) {
        enhancer.setSuperclass(interfaceClass);
        enhancer.setCallback(this);
        return (T) enhancer.create(type, args);
    }

    @Override
    public Object intercept(Object obj, Method method, Object[] args, MethodProxy methodProxy) throws Throwable {
        Object res = null;
        ShardedThrift<T> shardedThrift = null;
        boolean isTimeout = false;//判断是否是socket read超时异常
        try {
            shardedThrift = pool.getResource();
            T client = shardedThrift.getClient(this.clientClass);
            res = method.invoke(client, args);
        } catch (Exception e) {
            String ip = "";
            int port = 0;
            if (shardedThrift != null) {
                ip = shardedThrift.getInfo().getIp();
                port = shardedThrift.getInfo().getPort();
            }
            Throwable tmp = e.getCause();
            while (tmp != null) {
                if (tmp instanceof SocketException || tmp instanceof IOException) {
                    pool.close(shardedThrift, true);
                    isTimeout = true;
                    break;
                }
                if (tmp instanceof SocketTimeoutException) {
                    pool.close(shardedThrift, false);
                    isTimeout = true;
                    break;
                }
                tmp = tmp.getCause();
            }
            throw new SoaException("Server ip: " + ip + ",port: " + port, e);
        } finally {
            if (!isTimeout) {
                pool.close(shardedThrift, false);
            }
        }
        return res;
    }
}