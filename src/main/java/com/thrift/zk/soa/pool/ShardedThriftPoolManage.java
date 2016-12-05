
package com.thrift.zk.soa.pool;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Author: xiajun
 * Date: 15/11/02 14:50
 */
public class ShardedThriftPoolManage {
    private final static Logger LOGGER = LoggerFactory.getLogger(ShardedThriftPoolManage.class);
    private ClusterPool pool;

    public void setPool(ClusterPool pool) {
        this.pool = pool;
    }

    /**
     * 当服务器节点被删除时，要对连接池中的对象进行有效性检查
     *
     * @param shardid
     */
    public void deletePoolObject(String shardid) {
        if (pool != null) {
            try {
                pool.clear(shardid);
                LOGGER.warn("Rpc server node close " + shardid);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 根据服务节点数量计算最大连接数
     *
     * @param nodes
     */
    public void setMaxTotal(int nodes) {
        if (pool != null && nodes > 0) {
            int mtpk = pool.getMaxTotalPerKey();
            int max = mtpk * nodes;
            max = max > 5000 ? 5000 : max;//连接数不能大于5000
            pool.setMaxTotal(max);
        }
    }
}
