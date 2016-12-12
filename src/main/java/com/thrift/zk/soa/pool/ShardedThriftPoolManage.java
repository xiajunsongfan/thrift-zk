package com.thrift.zk.soa.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: xiajun
 * Date: 16/11/02 14:50
 */
public class ShardedThriftPoolManage {
    private final static Logger LOGGER = LoggerFactory.getLogger(ShardedThriftPoolManage.class);
    private ClusterPool pool;

    public void setPool(ClusterPool pool) {
        this.pool = pool;
    }

    /**
     * 删除服务器连接。
     * 当服务器节点被删除时，要对连接池中的对象进行有效性检查
     *
     * @param shardid 服务节点
     */
    public void deletePoolObject(String shardid) {
        if (pool != null) {
            try {
                pool.clear(shardid);
                LOGGER.warn("Rpc server node {} close.", shardid);
            } catch (Exception e) {
                LOGGER.error("Rpc server node {} close.", shardid, e);
            }
        }
    }
}
