
package com.thrift.zk.soa.thrift.route;

import com.thrift.zk.soa.thrift.NodeInfo;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 轮询服务节点
 * Author: xiajun
 * Date: 15/12/17 12:12
 */
public class RotationRoute extends RpcRoute {
    private AtomicInteger index = new AtomicInteger(0);
    protected ReadWriteLock lock = new ReentrantReadWriteLock();

    @Override
    public boolean addServerNode(NodeInfo node) {
        lock.writeLock().lock();
        try {
            serverNodes.put(node.getPath(), node);
            nodes.clear();
            nodes.addAll(serverNodes.values());
        } finally {
            lock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public boolean removeServerNode(String path) {
        boolean res = false;
        lock.writeLock().lock();
        try {
            if (serverNodes.remove(path) != null) {
                nodes.clear();
                nodes.addAll(serverNodes.values());
                res = true;
            }
        } finally {
            lock.writeLock().unlock();
        }
        return res;
    }

    @Override
    public NodeInfo getServer() {
        lock.readLock().lock();
        try {
            if (serverNodes.isEmpty()) {
                return null;
            }
            int i = Math.abs(index.incrementAndGet());
            int key = i % serverNodes.size();
            return nodes.get(key);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public int size() {
        lock.readLock().lock();
        try {
            return serverNodes.size();
        } finally {
            lock.readLock().unlock();
        }
    }
}
