package com.thrift.zk.soa.thrift.route;

import com.thrift.zk.soa.thrift.NodeInfo;

import java.util.concurrent.atomic.AtomicInteger;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * 轮询服务节点
 * Author: xiajun
 * Date: 16/12/01 12:12
 */
public class RotationRoute extends RpcRoute {
    private AtomicInteger index = new AtomicInteger(0);

    @Override
    public boolean addServerNode(NodeInfo node) {
        serverNodes.put(node.getPath(), node);
        nodes.add(node);
        return true;
    }

    @Override
    public boolean removeServerNode(String path) {
        boolean res = false;
        NodeInfo info = serverNodes.remove(path);
        if (info != null) {
            nodes.remove(info);
            res = true;
        }
        return res;
    }

    @Override
    public NodeInfo getServer() {
        if (serverNodes.isEmpty()) {
            return null;
        }
        int i = Math.abs(index.incrementAndGet());
        int key = i % serverNodes.size();
        return nodes.get(key);
    }

    @Override
    public int size() {
        return serverNodes.size();
    }
}
