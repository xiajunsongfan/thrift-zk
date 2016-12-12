/* * Copyright (c) 2016. mogujie */package com.mogujie.recsys.soa;import com.thrift.zk.soa.pool.ThriftPoolConfig;import com.thrift.zk.soa.thrift.client.ThriftClient;import com.thrift.zk.soa.thrift.route.RouteEnum;import com.thrift.zk.soa.utils.Constant;import com.mogujie.recsys.soa.idl.UserService;import org.apache.thrift.TException;import org.junit.Test;/** * Author: xiajungetResult * Date: 16/11/01 03:31 */public class ThriftClientTest {    @Test    public void test() throws TException, InterruptedException {        ThriftPoolConfig config = new ThriftPoolConfig();        config.setZkAddress("127.0.0.1:2181");//zookeeper集群地址        config.setJdns("/jdns/test/test_server");//服务端使用的节点        config.setZkConnTimeout(60000);        config.setConnTotal(3);//client会对每个server创建最多10个连接        //config.setMaxTotal(100);        config.setUseZk(true);//使用zookeeper管理client        //config.setHosts("127.0.0.1:9091"); //本地模式时直连rpc服务        //config.setProtocol(Constant.Protocol.TBINARYPROTOCOL);//thrift通信压缩模式        // config.setClientClass(UserInfo.Client.class);//本地模式时使用，设置client        //config.setRoute(RouteEnum.ROTATION);//客户端设置的路由策略，如果服务端设置过，这次设置将被服务端的设置覆盖        ThriftClient thriftClient = new ThriftClient(config);        //UserInfo.Iface client = (UserInfo.Iface) thriftClient.getClient();//获取thrift客户端        final UserService.Iface client = (UserService.Iface) thriftClient.getClient();        for (int i = 0; i < 12; i++) {            client.getSex();        }        System.out.println("---------");    }}