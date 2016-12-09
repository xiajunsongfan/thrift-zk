
package com.thrift.zk.soa.thrift.client;

import com.thrift.zk.soa.pool.ThriftPoolConfig;
import com.thrift.zk.soa.utils.Constant;
import com.thrift.zk.soa.thrift.NodeInfo;
import com.thrift.zk.soa.zookeeper.ThriftZkManage;

/**
 * Author: xiajun
 * Date: 15/10/31 23:20
 */
public class ThriftShardedInfo {
    private ThriftZkManage sharded;
    private ThriftPoolConfig config;

    public ThriftShardedInfo(ThriftZkManage sharded, ThriftPoolConfig config) {
        this.sharded = sharded;
        this.config = config;
    }

    public NodeInfo getNodeInfo() {
        return sharded.poll();
    }

    public NodeInfo getNodeInfo(String key) {
        return sharded.poll(key);
    }

    public ThriftPoolConfig getConfig() {
        return config;
    }

    public int getTimeout() {
        return config.getReadTimeout();
    }

    public Class[] getClient() {
        return config.getClientClass();
    }

    public Constant.Protocol getProtocol() {
        return config.getProtocol();
    }
}
