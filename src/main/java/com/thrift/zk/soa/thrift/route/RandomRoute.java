
package com.thrift.zk.soa.thrift.route;

import com.thrift.zk.soa.thrift.NodeInfo;

import java.util.Random;

/**
 * 随机获取一个服务节点
 * Author: xiajun
 * Date: 15/12/17 12:18
 */
public class RandomRoute extends RotationRoute {
    private Random random = new Random();

    @Override
    public NodeInfo getServer() {
        lock.readLock().lock();
        try {
            if (serverNodes.isEmpty()) {
                return null;
            }
            int r = random.nextInt(1000000);
            int index = r % serverNodes.size();
            return nodes.get(index);//如果nodes的数据和serverNodes对应不上将会报错
        } finally {
            lock.readLock().unlock();
        }
    }
}
