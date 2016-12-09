/* * Copyright (c) 2016. mogujie */package com.mogujie.recsys.soa;import com.thrift.zk.soa.thrift.route.RouteEnum;import com.thrift.zk.soa.thrift.server.ServerConfig;import com.thrift.zk.soa.thrift.server.ThriftServer;import com.thrift.zk.soa.utils.Constant;import com.mogujie.recsys.soa.idl.UserServiceImpl;import org.junit.Test;/** * Author: xiajun * Date: 16/11/01 02:37 */public class ThriftServerTest {    @Test    public void start() throws Exception {        ServerConfig sc = new ServerConfig();        sc.setZkAddress("10.13.128.214:2181").setDns("/jdns/si/server")                .setHost("0.0.0.0").setProtocol(Constant.Protocol.TCOMPACTPROTOCOL)                .setSelectorThreads(2)                .setWorkerThreads(120)                .setZkConnTimeout(60000).setRoute(RouteEnum.WEIGHT).setWeight(10)                .setPort(9092).setUseZk(true);        ThriftServer server = new ThriftServer(sc, new UserServiceImpl());        server.start();        Thread.sleep(1000000);    }}