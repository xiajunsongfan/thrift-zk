
package com.thrift.zk.soa.thrift.route;

import com.thrift.zk.soa.thrift.NodeInfo;
import com.thrift.zk.soa.utils.MurmurHash;

import java.util.Iterator;
import java.util.Random;
import java.util.SortedMap;
import java.util.TreeMap;
import java.util.concurrent.locks.ReadWriteLock;
import java.util.concurrent.locks.ReentrantReadWriteLock;

/**
 * Author: xiajun
 * Date: 15/12/18 14:43
 */
public class WeightRoute extends RpcRoute {
    private TreeMap<Long, String> nodes = new TreeMap<Long, String>();
    protected ReadWriteLock lock = new ReentrantReadWriteLock();
    private Random random = new Random();

    public NodeInfo getServer() {
        lock.readLock().lock();
        try {
            SortedMap<Long, String> tail = nodes.tailMap(random.nextLong());
            String ni;
            if (tail.isEmpty()) {
                ni = nodes.get(nodes.firstKey());
            } else {
                ni = tail.get(tail.firstKey());
            }
            return serverNodes.get(ni);
        } finally {
            lock.readLock().unlock();
        }
    }

    @Override
    public boolean addServerNode(NodeInfo node) {
        int w = 160 * node.getWeight();
        String key = node.getPath();
        lock.writeLock().lock();
        try {
            for (int i = 0; i < w; i++) {
                key = key + "-node-" + i;
                nodes.put(MurmurHash.hash64A(key.getBytes(), key.length()), node.getPath());
            }
            serverNodes.put(node.getPath(), node);
        } finally {
            lock.writeLock().unlock();
        }
        return true;
    }

    @Override
    public NodeInfo getServer(String key) {
        return serverNodes.get(key);
    }

    @Override
    public boolean removeServerNode(String key) {
        boolean res = false;
        lock.writeLock().lock();
        try {
            serverNodes.remove(key);
            Iterator<Long> it = nodes.keySet().iterator();
            while (it.hasNext()) {
                Long key_ = it.next();
                if (key.equals(nodes.get(key_))) {
                    it.remove();
                    res = true;
                }
            }
        } finally {
            lock.writeLock().unlock();
        }
        return res;
    }

    @Override
    public int size() {
        return serverNodes.size();
    }
}

