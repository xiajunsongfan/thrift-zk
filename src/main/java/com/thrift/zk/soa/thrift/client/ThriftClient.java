
package com.thrift.zk.soa.thrift.client;

import com.thrift.zk.soa.exception.SoaException;
import com.thrift.zk.soa.pool.*;
import com.thrift.zk.soa.zookeeper.ZkThriftSharded;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: xiajun
 * Date: 15/11/01 01:44
 */
public class ThriftClient<T> {
    private ThriftPoolConfig config;
    private ClusterPool<ShardedThrift> pool;
    private Map<Class, Object> clients;

    /**
     * ThriftClient应该是一个单例的，在同一个服务同一个进程中不应该出现2个
     * 该类实例
     *
     * @param config
     */
    public ThriftClient(ThriftPoolConfig config) {
        this.config = config;
        this.init();
    }

    /**
     * 初始化连接
     */
    public void init() {
        ShardedThriftPoolManage poolManage = new ShardedThriftPoolManage();
        ZkThriftSharded sharded;
        if (config.isUseZk()) {
            sharded = new ZkThriftSharded(config.getZkAddress(), config.getZkSessionTimeout(), config.getZkConnTimeout(), poolManage, config.getRoute());
            sharded.listen(config.getJdns());
        } else {
            sharded = new ZkThriftSharded(config.getHosts(), config.getRoute());
        }
        ThriftShardedInfo shardedInfo = new ThriftShardedInfo(sharded, config);
        pool = new ClusterThriftPool(config, new ClusterThriftFactory(shardedInfo));
        poolManage.setPool(pool);
        if (config.getClientClass() == null) {
            if (!config.isUseZk()) {
                throw new NullPointerException("Thrift client class is null.");
            }
            config.setClientClass(buildClientClass(sharded.getServerName(config.getJdns())));
        }
    }

    /**
     * 获取thrift rpc客户端
     *
     * @param clazz 客户端字节码，目前不支持异步客户端
     * @return
     */
    public T getClient(Class clazz) {
        if (clients == null) {
            clients = create(config.getClientClass());
        }
        Object obj = clients.get(clazz);
        if (obj == null) {
            throw new IllegalStateException("not found " + clazz.toString());
        }
        return (T) obj;
    }

    /**
     * 获取thrift rpc客户端
     *
     * @return
     */
    public T getClient() {
        if (config.getClientClass() != null) {
            return this.getClient(config.getClientClass()[0]);
        }
        throw new IllegalArgumentException("Client class not found.");
    }

    /**
     * 根据zookeeper获取jdns提供的服务
     *
     * @param serverClassNames
     * @return
     */
    private Class[] buildClientClass(String serverClassNames) {
        String[] names = serverClassNames.split(",");
        Class[] clazzs = new Class[names.length];
        for (int i = 0; i < names.length; i++) {
            String name = names[i];
            String clientClass = name + "$Client";
            try {
                clazzs[i] = Class.forName(clientClass);
            } catch (ClassNotFoundException e) {
                throw new SoaException("", e);
            }
        }
        return clazzs;
    }

    /**
     * 创建代理客户端
     *
     * @param client 客户端的Class
     * @return
     */
    private Map<Class, Object> create(Class[] client) {
        Map<Class, Object> map = new ConcurrentHashMap<Class, Object>();
        for (Class clazz : client) {
            ThriftClientProxy proxy = new ThriftClientProxy(pool, clazz);
            Class[] interfaceClass = clazz.getInterfaces();
            map.put(clazz, proxy.getProxy(interfaceClass[0], new Class[]{}, new Object[]{}));
        }
        return map;
    }
}
