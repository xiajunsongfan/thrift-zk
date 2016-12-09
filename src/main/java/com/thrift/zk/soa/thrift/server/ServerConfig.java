
package com.thrift.zk.soa.thrift.server;

import com.thrift.zk.soa.thrift.route.RouteEnum;
import com.thrift.zk.soa.utils.Constant;

import java.util.concurrent.ExecutorService;

/**
 * Author: xiajun
 * Date: 2016-10-22
 * Time: 12:46:00
 */
public class ServerConfig {
    private int port = 9090;//服务端口
    private int selectorThreads = 4;//thrift selector的线程数
    private int workerThreads = 500;//thrift 工作线程数
    private int maxReadBufferBytes = 1024000;//thrift 读取buffer大小
    private int acceptQueueSizePerThread = 100;//thrift 客户端连接请求的存放队列
    private String host = null;//服务绑定的IP地址
    private int clientTimeout = 1000;//thrift服务端读超时时间
    private String dns;//服务程序在zookeeper中的节点
    private String zkAddress;//zookeeper集群地址
    private int zkSessionTimeout = 3000;//zookeeper session超时
    private int zkConnTimeout = 3000;//zookeeper 连接超时
    private ExecutorService workerThreadPool;//thrift处理请求的线程池
    private boolean useZk = true;//是否使用zookeeper
    private String cluster = Constant.DEFAULT_CLUSTER;//默认队列
    private int weight;//服务器权重
    private RouteEnum route;
    private Constant.Protocol protocol = Constant.Protocol.TCOMPACTPROTOCOL;//thrift 使用的压缩协议

    public int getPort() {
        return port;
    }

    /**
     * 设置服务器端口号
     *
     * @param port
     * @return
     */
    public ServerConfig setPort(int port) {
        this.port = port;
        return this;
    }

    public int getSelectorThreads() {
        return selectorThreads;
    }

    /**
     * 设置thrift selector线程数量，根据实际情况设置，
     * 设置标准：服务响应时间偏大类型的运用可以将该值设大，相反请设小该值
     * 默认值：24
     *
     * @param selectorThreads
     * @return
     */
    public ServerConfig setSelectorThreads(int selectorThreads) {
        this.selectorThreads = selectorThreads;
        return this;
    }

    public int getWorkerThreads() {
        return workerThreads;
    }

    /**
     * 该参数为thrift最大工作线程数，工作线程是调用服务实现类的载体
     * 设置标准：参考 setSelectorThreads，两者相通
     * 默认值：500
     *
     * @param workerThreads
     * @return
     */
    public ServerConfig setWorkerThreads(int workerThreads) {
        this.workerThreads = workerThreads;
        return this;
    }

    public int getMaxReadBufferBytes() {
        return maxReadBufferBytes;
    }

    /**
     * 设置thrift server和client通信时一次最大能读的字节数
     * 默认值：1M，除非有特殊需求，否则请不要增大该值
     *
     * @param maxReadBufferBytes
     * @return
     */
    public ServerConfig setMaxReadBufferBytes(int maxReadBufferBytes) {
        this.maxReadBufferBytes = maxReadBufferBytes;
        return this;
    }

    public int getAcceptQueueSizePerThread() {
        return acceptQueueSizePerThread;
    }

    /**
     * 设置每个SelectorThread线程处理客户端请求时的队列大小
     * 默认值：100 一般情况一下不需要重设该值
     *
     * @param acceptQueueSizePerThread
     * @return
     */
    public ServerConfig setAcceptQueueSizePerThread(int acceptQueueSizePerThread) {
        this.acceptQueueSizePerThread = acceptQueueSizePerThread;
        return this;
    }

    public String getHost() {
        return host;
    }

    /**
     * 设置thrift服务绑定的IP
     * 默认值：程序会绑定到0.0.0.0上
     *
     * @param host
     * @return
     */
    public ServerConfig setHost(String host) {
        this.host = host;
        return this;
    }

    public int getClientTimeout() {
        return clientTimeout;
    }

    /**
     * 设置thrift 服务端socket读取客户端数据时的超时，读超时
     * 默认值：1000ms
     *
     * @param clientTimeout
     * @return
     */
    public ServerConfig setClientTimeout(int clientTimeout) {
        this.clientTimeout = clientTimeout;
        return this;
    }

    public String getDns() {
        return dns;
    }

    /**
     * 设置thrift服务在zookeeper上的注册地址，每个不同服务应该使用不同dns
     * 如果设置 setUseZk(false),该值可不设置
     *
     * @param dns
     * @return
     */
    public ServerConfig setDns(String dns) {
        this.dns = dns;
        return this;
    }

    public String getZkAddress() {
        return zkAddress;
    }

    /**
     * zookeeper集群地址,格式： ip:port,ip:port
     *
     * @param zkAddress
     * @return
     */
    public ServerConfig setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
        return this;
    }

    public ExecutorService getWorkerThreadPool() {
        return workerThreadPool;
    }

    /**
     * 设置thrift 服务 工作线程使用的线程池，如果设置了该值，那么setWorkerThreads()的设置将无效
     *
     * @param workerThreadPool
     * @return
     */
    public ServerConfig setWorkerThreadPool(ExecutorService workerThreadPool) {
        this.workerThreadPool = workerThreadPool;
        return this;
    }

    public boolean isUseZk() {
        return useZk;
    }

    /**
     * 设置是否使用zookeeper对thrift服务进行管理
     * true表示使用zookeeper，当为false时 意味着这个服务是个单机或者叫本地服务
     *
     * @param useZk
     * @return
     */
    public ServerConfig setUseZk(boolean useZk) {
        this.useZk = useZk;
        return this;
    }

    public String getCluster() {
        return cluster;
    }

    /**
     * 集群名称 暂时无实际用途
     *
     * @param cluster
     * @return
     */
    public ServerConfig setCluster(String cluster) {
        this.cluster = cluster;
        return this;
    }

    public Constant.Protocol getProtocol() {
        return protocol;
    }

    /**
     * 设置thrift 服务端使用的通信压缩模式
     * 默认值：TCompactProtocol
     *
     * @param protocol
     * @return
     */
    public ServerConfig setProtocol(Constant.Protocol protocol) {
        this.protocol = protocol;
        return this;
    }

    public int getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    /**
     * 设置zookeeper 会话超时时间
     *
     * @param zkSessionTimeout
     * @return
     */
    public ServerConfig setZkSessionTimeout(int zkSessionTimeout) {
        this.zkSessionTimeout = zkSessionTimeout;
        return this;
    }

    public int getZkConnTimeout() {
        return zkConnTimeout;
    }

    /**
     * 设置zookeeper连接超时时间
     *
     * @param zkConnTimeout
     * @return
     */
    public ServerConfig setZkConnTimeout(int zkConnTimeout) {
        this.zkConnTimeout = zkConnTimeout;
        return this;
    }

    public int getWeight() {
        return weight;
    }

    /**
     * 服务器权重,权重越大需要处理的请求越多,目前权重范围[1-10]
     *
     * @param weight
     */
    public ServerConfig setWeight(int weight) {
        this.weight = weight;
        return this;
    }

    public RouteEnum getRoute() {
        return route;
    }

    /**
     * 设置分流方式
     * default: RouteEnum.RANDOM
     * @param route
     * @return
     */
    public ServerConfig setRoute(RouteEnum route) {
        this.route = route;
        return this;
    }
}
