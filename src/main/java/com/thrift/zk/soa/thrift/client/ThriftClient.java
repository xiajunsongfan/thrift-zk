
package com.thrift.zk.soa.thrift.client;

import com.thrift.zk.soa.exception.SoaException;
import com.thrift.zk.soa.pool.*;
import com.thrift.zk.soa.thrift.server.ServerRegisterInfo;
import com.thrift.zk.soa.utils.Constant;
import com.thrift.zk.soa.zookeeper.ThriftZkManage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Author: xiajun
 * Date: 16/11/01 01:44
 */
public class ThriftClient<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ThriftClient.class);
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
        ThriftZkManage zkManage;
        if (config.isUseZk()) {
            zkManage = new ThriftZkManage(config.getZkAddress(), config.getZkSessionTimeout(), config.getZkConnTimeout(), poolManage, config.getRoute().getRoute());
            zkManage.listen(config.getJdns());
        } else {
            zkManage = new ThriftZkManage(config.getHosts(), config.getRoute().getRoute());
        }
        ThriftShardedInfo shardedInfo = new ThriftShardedInfo(zkManage, config);
        pool = new ClusterThriftPool(config, new ClusterThriftFactory(shardedInfo));
        poolManage.setPool(pool);
        if (config.getClientClass() == null) {
            if (!config.isUseZk()) {
                throw new NullPointerException("Thrift client class is null.");
            }
            ServerRegisterInfo sri = zkManage.getServerName(config.getJdns());
            config.setClientClass(buildClientClass(sri.getClassName()));
            if (sri.getRoute() != null) {
                zkManage.setRoute(sri.getRoute().getRoute());
                LOGGER.warn("Client set route invalid,Use the server Settings route={}", sri.getRoute().getValue());
                config.setRoute(sri.getRoute());
            }
            if (config.getProtocol() != sri.getProtocol()) {
                Constant.Protocol tmp = config.getProtocol();
                config.setProtocol(sri.getProtocol());
                LOGGER.warn("Server using the {} protocol,Clients set {} protocol", sri.getProtocol().getProtocol(), tmp.getProtocol());
            }
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
