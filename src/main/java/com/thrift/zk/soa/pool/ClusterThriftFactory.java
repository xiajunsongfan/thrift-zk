package com.thrift.zk.soa.pool;

import com.thrift.zk.soa.thrift.client.ShardedThrift;
import com.thrift.zk.soa.thrift.client.ThriftShardedInfo;
import org.apache.commons.pool2.KeyedPooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 * Author: xiajun
 * Date: 16/02/01 14:05
 */
public class ClusterThriftFactory<T> implements KeyedPooledObjectFactory<String, ShardedThrift> {
    private ThriftShardedInfo shardedInfo;

    public ClusterThriftFactory(ThriftShardedInfo shardedInfo) {
        this.shardedInfo = shardedInfo;
    }

    @Override
    public PooledObject<ShardedThrift> makeObject(String key) throws Exception {
        ShardedThrift<T> thrift = new ShardedThrift<T>(shardedInfo, key);
        return new DefaultPooledObject<ShardedThrift>(thrift);
    }

    @Override
    public void destroyObject(String key, PooledObject<ShardedThrift> p) throws Exception {
        ShardedThrift<T> thrift = p.getObject();
        thrift.close();
    }

    @Override
    public boolean validateObject(String key, PooledObject<ShardedThrift> p) {
        ShardedThrift<T> thrift = p.getObject();
        return thrift.validate();
    }

    @Override
    public void activateObject(String key, PooledObject<ShardedThrift> p) throws Exception {

    }

    @Override
    public void passivateObject(String key, PooledObject<ShardedThrift> p) throws Exception {

    }
}
