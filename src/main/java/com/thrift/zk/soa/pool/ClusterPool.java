
package com.thrift.zk.soa.pool;

import com.thrift.zk.soa.exception.SoaException;
import com.thrift.zk.soa.exception.ThriftConnectionException;
import com.thrift.zk.soa.thrift.NodeInfo;
import com.thrift.zk.soa.thrift.client.route.RpcRoute;
import org.apache.commons.pool2.impl.GenericKeyedObjectPool;

/**
 * Author:  - xiajun
 * Date: 16/02/01 14:17
 */
public abstract class ClusterPool<T> {
    protected GenericKeyedObjectPool<String, T> thriftPool;
    private RpcRoute route;

    public ClusterPool(final ThriftPoolConfig poolConfig, ClusterThriftFactory factory) {
        this.route = poolConfig.getRoute();
        initPool(poolConfig, factory);
    }

    /**
     * 初始化连接池
     *
     * @param poolConfig
     * @param factory
     */
    public void initPool(final ThriftPoolConfig poolConfig, ClusterThriftFactory factory) {
        if (this.thriftPool != null) {
            try {
                closePool();
            } catch (Exception e) {
            }
        }
        this.thriftPool = new GenericKeyedObjectPool<String, T>(factory, poolConfig);
    }

    /**
     * 获取连接
     *
     * @return
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
     * @param resource
     */
    protected void returnResourceObject(final String key, final T resource) {
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
     * @param resource
     */
    protected void returnBrokenResource(final String key, final T resource) {
        if (resource != null) {
            returnBrokenResourceObject(key, resource);
        }
    }

    /**
     * 返回有效连接，回收到连接池
     *
     * @param resource
     */
    protected void returnResource(final String key, final T resource) {
        if (resource != null) {
            returnResourceObject(key, resource);
        }
    }

    /**
     * 回收异常的连接，将其销毁
     *
     * @param resource
     */
    protected void returnBrokenResourceObject(final String key, final T resource) {
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

    protected void closePool() {
        try {
            thriftPool.close();
        } catch (Exception e) {
            throw new SoaException("Could not destroy the pool", e);
        }
    }

    public int getNumActive() {
        if (poolInactive()) {
            return -1;
        }

        return this.thriftPool.getNumActive();
    }

    public int getNumIdle() {
        if (poolInactive()) {
            return -1;
        }

        return this.thriftPool.getNumIdle();
    }

    public int getNumWaiters() {
        if (poolInactive()) {
            return -1;
        }

        return this.thriftPool.getNumWaiters();
    }

    public long getMeanBorrowWaitTimeMillis() {
        if (poolInactive()) {
            return -1;
        }

        return this.thriftPool.getMeanBorrowWaitTimeMillis();
    }

    public long getMaxBorrowWaitTimeMillis() {
        if (poolInactive()) {
            return -1;
        }
        return this.thriftPool.getMaxBorrowWaitTimeMillis();
    }

    private boolean poolInactive() {
        return this.thriftPool == null || this.thriftPool.isClosed();
    }

    /**
     * 立即驱逐失效连接
     *
     * @throws Exception
     */
    public void evict() throws Exception {
        thriftPool.evict();
    }

    public void clear(String key) {
        thriftPool.clear(key);
    }

    public int getMaxTotalPerKey() {
        return thriftPool.getMaxTotalPerKey();
    }

    public void setMaxTotal(int total) {
        thriftPool.setMaxTotal(total);
    }
    public int getMaxTotal(){
        return thriftPool.getMaxTotal();
    }
    public boolean isClosed() {
        return this.thriftPool.isClosed();
    }
}
