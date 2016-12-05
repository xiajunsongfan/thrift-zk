
package com.thrift.zk.soa.pool;

import com.thrift.zk.soa.thrift.NodeInfo;
import com.thrift.zk.soa.thrift.client.ShardedThrift;
import com.thrift.zk.soa.thrift.client.route.RpcRoute;

/**
 * Author: xiajun
 * Date: 16/02/01 14:12
 */
public class ClusterThriftPool extends ClusterPool<ShardedThrift> {

    public ClusterThriftPool(ThriftPoolConfig poolConfig, ClusterThriftFactory<ShardedThrift> factory) {
        super(poolConfig, factory);
    }

    @Override
    public void close(ShardedThrift resource, boolean force) {
        if (resource != null && resource.getInfo() != null) {
            NodeInfo info = resource.getInfo();
            if (force) {
                this.returnBrokenResource(info.getPath(), resource);
            } else {
                if (resource.validate()) {
                    this.returnResource(info.getPath(), resource);
                } else {
                    this.returnBrokenResource(info.getPath(), resource);
                }
            }
        }
    }
}
