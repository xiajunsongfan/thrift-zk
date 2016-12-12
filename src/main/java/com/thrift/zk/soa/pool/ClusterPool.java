package com.thrift.zk.soa.pool;

import com.thrift.zk.soa.exception.SoaException;
import com.thrift.zk.soa.exception.ThriftConnectionException;
import com.thrift.zk.soa.thrift.NodeInfo;
import com.thrift.zk.soa.thrift.route.RpcRoute;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: xiajun
 * Date: 16/02/01 14:17
 */
public abstract class ClusterPool<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ClusterPool.class);
    private GenericKeyedObjectPool<String, T> thriftPool;
    private RpcRoute route;

    ClusterPool(final ThriftPoolConfig poolConfig, ClusterThriftFactory factory) {
        this.route = poolConfig.getRoute().getRoute();
        initPool(poolConfig, factory);
    }

    /**
     * 初始化连接池
     *
     * @param poolConfig 连接池配置信息
     * @param factory    对象创建工厂
     */
    private void initPool(final ThriftPoolConfig poolConfig, ClusterThriftFactory factory) {
        if (this.thriftPool != null) {
            try {
                closePool();
            } catch (Exception e) {
                LOGGER.error("Init fail.", e);
            }
        }
        this.thriftPool = new GenericKeyedObjectPool<String, T>(factory, poolConfig);
    }

    /**
     * 获取连接
     *
     * @return T
     */
    public T getResource() {
        if (this.route == null) {
            throw new NullPointerException("Route is null.");
        }
        try {
            NodeInfo info = route.getServer();
            if (info == null) {
                throw new NullPointerException("not found thrift server node.");
            }
            return thriftPool.borrowObject(info.getPath());
        } catch (Exception e) {
            throw new ThriftConnectionException("Could not get a resource from the pool,maxTotal:" + thriftPool.getMaxTotal(), e);
        }
    }

    /**
     * 返回有效连接，回收到连接池
     *
     * @param resource 连接对象
     */
    private void returnResourceObject(final String key, final T resource) {
        if (resource == null) {
            return;
        }
        try {
            thriftPool.returnObject(key, resource);
        } catch (Exception e) {
            throw new SoaException("Could not return the resource to the pool", e);
        }
    }

    /**
     * 回收异常的连接，将其销毁
     *
     * @param resource 连接对象
     */
    void returnBrokenResource(final String key, final T resource) {
        if (resource != null) {
            returnBrokenResourceObject(key, resource);
        }
    }

    /**
     * 返回有效连接，回收到连接池
     *
     * @param resource 连接对象
     */
    void returnResource(final String key, final T resource) {
        if (resource != null) {
            returnResourceObject(key, resource);
        }
    }

    /**
     * 回收异常的连接，将其销毁
     *
     * @param resource 连接对象
     */
    private void returnBrokenResourceObject(final String key, final T resource) {
        try {
            thriftPool.invalidateObject(key, resource);
        } catch (Exception e) {
            throw new SoaException("Could not return the resource to the pool", e);
        }
    }

    /**
     * 回收资源
     *
     * @param resource 连接资源
     * @param force    强制删除
     */
    public abstract void close(final T resource, boolean force);

    public void destroy() {
        closePool();
    }

    private void closePool() {
        try {
            thriftPool.close();
        } catch (Exception e) {
            throw new SoaException("Could not destroy the pool", e);
        }
    }

    /**
     * 立即驱逐失效连接
     *
     * @throws Exception e
     */
    public void evict() throws Exception {
        thriftPool.evict();
    }

    /**
     * 删除一个key下的所有连接
     *
     * @param key 资源key
     */
    void clear(String key) {
        thriftPool.clear(key);
    }

    /**
     * 获取连接池的总大小
     */
    int getMaxTotal() {
        return thriftPool.getMaxTotal();
    }
}
