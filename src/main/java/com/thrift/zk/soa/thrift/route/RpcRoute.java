package com.thrift.zk.soa.thrift.route;

import com.thrift.zk.soa.thrift.NodeInfo;

import java.util.*;

/**
 * 漏油选取服务器
 * 注意线程安全问题
 * Author: xiajun
 * Date: 16/12/01 11:55
 */
public abstract class RpcRoute {
    protected Map<String, NodeInfo> serverNodes = new HashMap<String, NodeInfo>();//存放所有服务节点，创建对象时从这里提取
    protected  List<NodeInfo> nodes = new ArrayList<NodeInfo>(10);//存放所有节点数据

    /**
     * 添加一个服务节点
     *
     * @param node
     */
    public abstract boolean addServerNode(NodeInfo node);

    /**
     * 删除一个服务节点
     *
     * @param path 节点地址
     */
    public abstract boolean removeServerNode(String path);

    /**
     * 获取一个服务节点
     *
     * @return
     */
    public abstract NodeInfo getServer();

    /**
     * 根据指定的key获取服务节点
     *
     * @param key
     * @return
     */
    public NodeInfo getServer(String key) {
        return serverNodes.get(key);
    }

    /**
     * 获取服务节点数
     *
     * @return
     */
    public abstract int size();
}
