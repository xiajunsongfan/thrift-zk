package com.thrift.zk.soa.thrift.client;

import com.thrift.zk.soa.exception.SoaException;
import com.thrift.zk.soa.utils.Constant;
import com.thrift.zk.soa.thrift.NodeInfo;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TMultiplexedProtocol;
import org.apache.thrift.protocol.TProtocol;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TSocket;
import org.apache.thrift.transport.TTransport;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.util.HashMap;
import java.util.Map;

/**
 * Author: xiajun
 * Date: 16/10/31 22:45
 */
public class ShardedThrift<T> {
    private final static Logger LOGGER = LoggerFactory.getLogger(ShardedThrift.class);
    private ThriftShardedInfo shardedInfo;
    private TTransport transport;
    private TSocket tSocket;
    private NodeInfo info;
    private Map<Class, T> clients = new HashMap<Class, T>();
    private String key;//连接池使用的是keyObject

    public ShardedThrift(ThriftShardedInfo shardedInfo, String key) {
        this.shardedInfo = shardedInfo;
        this.key = key;
        this.connection(shardedInfo.getClient());
    }

    public ShardedThrift(ThriftShardedInfo shardedInfo) {
        this(shardedInfo, null);
    }

    private void connection(Class[] clientClass) {
        if (key == null) {
            info = this.shardedInfo.getNodeInfo();
        } else {
            info = this.shardedInfo.getNodeInfo(key);
        }
        tSocket = new TSocket(info.getIp(), info.getPort(), shardedInfo.getTimeout());
        transport = new TFramedTransport(tSocket);
        try {
            transport.open();
            TProtocol protocol;
            if (Constant.Protocol.TBINARYPROTOCOL == shardedInfo.getProtocol()) {
                protocol = new TBinaryProtocol(transport);
            } else {
                protocol = new TCompactProtocol(transport);
            }
            if (clientClass.length > 1) {
                createMultiClient(clientClass, protocol);
            } else {
                createClient(clientClass[0], protocol);
            }
            LOGGER.info("Create sync client success,ip : {}, port : {}", info.getIp(), info.getPort());

        } catch (Exception e) {
            throw new SoaException("Create thrift client fail,cliet:" + clientClass[0].getName(), e);
        }
    }

    private void createClient(Class clientClas, TProtocol protocol) throws Exception {
        Constructor constructor = clientClas.getConstructor(TProtocol.class);
        clients.put(clientClas, (T) constructor.newInstance(protocol));
    }

    private void createMultiClient(Class[] clientClass, TProtocol protocol) throws Exception {
        for (Class clientClas : clientClass) {
            Constructor constructor = clientClas.getConstructor(TProtocol.class);
            TMultiplexedProtocol mp = new TMultiplexedProtocol(protocol, getClientName(clientClas));
            clients.put(clientClas, (T) constructor.newInstance(mp));
        }
    }

    public T getClient(Class clientClass) {
        return clients.get(clientClass);
    }

    public void close() {
        if (transport != null && transport.isOpen()) {
            transport.close();
        }
        if (tSocket != null && tSocket.isOpen()) {
            tSocket.close();
        }
        LOGGER.warn("Destory client, ip : {}, port : {}.", info.getIp(), info.getPort());
    }

    public boolean validate() {
        if (transport != null && tSocket != null) {
            return transport.isOpen() && tSocket.isOpen();
        }
        return false;
    }

    private String getClientName(Class clazz) {
        String[] packages = clazz.getCanonicalName().split("\\.");
        if (packages.length > 1) {
            return packages[packages.length - 2];
        }
        return null;
    }

    public NodeInfo getInfo() {
        return info;
    }

    public String getShardedId() {
        return info.getPath();
    }
}
