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
 * Date: 16/12/02 14:43
 */
public class WeightRoute extends RpcRoute {
    private TreeMap<Long, String> nodes = new TreeMap<Long, String>();
    protected ReadWriteLock lock = new ReentrantReadWriteLock();
    private Random random = new Random();

    public NodeInfo getServer() {
        SortedMap<Long, String> tail = nodes.tailMap(random.nextLong());
        String ni;
        if (tail.isEmpty()) {
            ni = nodes.get(nodes.firstKey());
        } else {
            ni = tail.get(tail.firstKey());
        }
        return serverNodes.get(ni);
    }

    @Override
    public boolean addServerNode(NodeInfo node) {
        int w = 160 * node.getWeight();
        String key = node.getPath();
        lock.writeLock().lock();
        try {
            serverNodes.put(node.getPath(), node);
            TreeMap<Long, String> newNodes = new TreeMap<Long, String>(nodes);
            for (int i = 0; i < w; i++) {
                key = key + "-node-" + i;
                newNodes.put(MurmurHash.hash64A(key.getBytes(), key.length()), node.getPath());
            }
            nodes = newNodes;
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
            TreeMap<Long, String> newNodes = new TreeMap<Long, String>();
            Iterator<Long> it = nodes.keySet().iterator();
            while (it.hasNext()) {
                Long key_ = it.next();
                String node = nodes.get(key_);
                if (!key.equals(node)) {
                    newNodes.put(key_, node);
                }
            }
            if (newNodes.size() != nodes.size()) {
                res = true;
            }
            nodes = newNodes;
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

