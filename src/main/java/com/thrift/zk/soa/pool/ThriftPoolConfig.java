package com.thrift.zk.soa.pool;

import com.thrift.zk.soa.thrift.route.RouteEnum;
import com.thrift.zk.soa.utils.Constant;
import org.apache.commons.pool2.impl.GenericKeyedObjectPoolConfig;

/**
 * Author: xiajun
 * Date: 16/02/01 14:32
 */
public class ThriftPoolConfig extends GenericKeyedObjectPoolConfig {
    private Class[] clientClass;
    private int readTimeout = 1000;
    private Constant.Protocol protocol = Constant.Protocol.TCOMPACTPROTOCOL;//thrift 通信数据压缩方式
    private String zkAddress;//zookeeper 集群地址
    private int zkSessionTimeout = 3000;
    private int zkConnTimeout = 5000;
    private String jdns;
    private String hosts;//直连rpc时使用
    private boolean useZk;
    private RouteEnum route = RouteEnum.RANDOM;

    public ThriftPoolConfig() {
        //此处设置的参数不建议覆盖，除非你非常了解common－pool和此程序
        this.setTestWhileIdle(true);
        this.setMinEvictableIdleTimeMillis(240000);
        this.setTimeBetweenEvictionRunsMillis(60000);
        this.setNumTestsPerEvictionRun(-1);
        this.setMaxWaitMillis(200);
        this.setTestOnBorrow(true);
        this.setMinIdlePerKey(1);
        this.setMaxIdlePerKey(-1);
    }

    public Class[] getClientClass() {
        return clientClass;
    }

    /**
     * 此参数设置在不使用zookeeper模式下必须设置，使用zookeeper时不建议设置
     * 可以设置一个或多个client，这要根据服务端启动的模式来决定，
     * 服务端启动时是多server模式，客户端应该对应使用多client模式，
     *
     * @param clientClass 接口类
     */
    public void setClientClass(Class... clientClass) {
        this.clientClass = clientClass;
    }

    public int getReadTimeout() {
        return readTimeout;
    }

    /**
     * thrift客户端调用rpc时的超时，
     * 默认值 1s
     *
     * @param readTimeout 读取超时
     */
    public void setReadTimeout(int readTimeout) {
        this.readTimeout = readTimeout;
    }

    public Constant.Protocol getProtocol() {
        return protocol;
    }

    /**
     * thirft通信压缩模式
     * 默认值 TCompactProtocol，可选值 TBinaryProtocol
     *
     * @param protocol thrift协议
     */
    public void setProtocol(Constant.Protocol protocol) {
        this.protocol = protocol;
    }

    public String getZkAddress() {
        return zkAddress;
    }

    /**
     * 设置zookeeper集群地址
     * 地址格式 ip:port,ip:port
     *
     * @param zkAddress zookeeper地址
     */
    public void setZkAddress(String zkAddress) {
        this.zkAddress = zkAddress;
    }

    public int getZkSessionTimeout() {
        return zkSessionTimeout;
    }

    /**
     * 设置zookeeper 会话超时时间
     * 默认值 3000ms
     *
     * @param zkSessionTimeout  zookeeper 会话超时
     */
    public void setZkSessionTimeout(int zkSessionTimeout) {
        this.zkSessionTimeout = zkSessionTimeout;
    }

    public int getZkConnTimeout() {
        return zkConnTimeout;
    }

    public String getJdns() {
        return jdns;
    }

    public void setJdns(String jdns) {
        this.jdns = jdns;
    }

    /**
     * 设置zookeeper连接超时
     * 默认值 5000ms
     *
     * @param zkConnTimeout zookeeper连接超时
     */
    public void setZkConnTimeout(int zkConnTimeout) {
        this.zkConnTimeout = zkConnTimeout;
    }

    /**
     * 每个客户端最大连接数
     * 默认为－1，表示没有限制
     *
     * @param maxTotal 客户端最大连接数
     */
    public void setMaxTotal(int maxTotal) {
        super.setMaxTotal(maxTotal);
    }

    /**
     * 与每台server建立的socket连接数，注意不是总连接数，
     * 3台server：总连接数 ＝ 3 * connTotal
     * 不能超过最大连接数
     *
     * @param connTotal 连接到每台服务器的最大连接数
     */
    public void setConnTotal(int connTotal) {
        super.setMaxTotalPerKey(connTotal);
    }

    /**
     * 最大空闲数应该和最大连接数使用一样的配置
     *
     * @param maxIdlePerKey 最大空闲数
     */
    public void setMaxIdlePerKey(int maxIdlePerKey) {
        super.setMaxIdlePerKey(-1);
    }

    /**
     * 返回连接池时检测是无意义的因此注掉该功能
     * 设置任何值都是无效的。
     *
     * @param testOnReturn 是否检测
     */
    @Deprecated
    public void setTestOnReturn(boolean testOnReturn) {
        super.setTestOnReturn(false);//返回对象时禁止检查
    }

    /**
     * 由于使用zookeeper时必须开启检查功能
     * 所以该方法被重写，设置任何值都是无效的。
     *
     * @param testWhileIdle 是否检测失效连接
     */
    public void setTestWhileIdle(boolean testWhileIdle) {
        super.setTestWhileIdle(true);//禁止关闭检查，连接池会定期检查失效连接，不允许关闭该选项
    }

    public String getHosts() {
        return hosts;
    }

    /**
     * 直连rpc时设置的服务IP:port地址
     *
     * @param hosts ip:port,ip:port
     */
    public void setHosts(String hosts) {
        this.hosts = hosts;
    }

    public boolean isUseZk() {
        return useZk;
    }

    /**
     * 是否使用zookeeper，false为不使用，
     * 不使用zookeeper就意味着是直连rpc方式
     *
     * @param useZk true 使用zookeeper管理，false表示不使用zookeeper管理
     */
    public void setUseZk(boolean useZk) {
        this.useZk = useZk;
    }

    public RouteEnum getRoute() {
        return route;
    }

    /**
     * 设置RPC在查询服务是使用的路由方式，默认轮询
     *
     * @param route 路由方式
     */
    public void setRoute(RouteEnum route) {
        this.route = route;
    }
}
