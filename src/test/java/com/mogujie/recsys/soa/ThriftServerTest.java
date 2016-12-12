/* * Copyright (c) 2016. mogujie */package com.mogujie.recsys.soa;import com.thrift.zk.soa.thrift.route.RouteEnum;import com.thrift.zk.soa.thrift.server.ServerConfig;import com.thrift.zk.soa.thrift.server.ThriftServer;import com.thrift.zk.soa.utils.Constant;import com.mogujie.recsys.soa.idl.UserServiceImpl;import org.junit.Test;/** * Author: xiajun * Date: 16/11/01 02:37 */public class ThriftServerTest {    @Test    public void start() throws Exception {        ServerConfig sc = new ServerConfig();        sc.setZkAddress("127.0.0.1:2181").setDns("/jdns/test/test_server")                .setHost("0.0.0.0").setProtocol(Constant.Protocol.TCOMPACTPROTOCOL)                .setSelectorThreads(2)                .setWorkerThreads(20)                .setZkConnTimeout(10000).setRoute(RouteEnum.ROTATION).setWeight(20)                .setPort(9092).setUseZk(true);        ThriftServer server = new ThriftServer(sc, new UserServiceImpl());        server.start();        Thread.sleep(1000000);    }}