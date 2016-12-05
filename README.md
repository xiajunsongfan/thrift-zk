## thrift-zk
---
###项目介绍：  
集成zookeeper thrift, 服务端可多机部署，客户端自动发现服务上下线，目前只实现了轮询分流


###模块使用：
	
	thrift server:
	public static void main(String[] args) throws Exception {
        ServerConfig sc = new ServerConfig();
        sc.setZkAddress("10.1.128.212:2181") //zookeeper 地址
                .setDns("/jdns/si/server")    //DNS节点，服务唯一标识
                .setHost("0.0.0.0")           //服务绑定IP
                .setPort(9091)                //服务绑定端口
                .setProtocol(Constant.Protocol.TCOMPACTPROTOCOL)//服务通信使用的压缩模式
                .setSelectorThreads(4)         //服务通信处理线程
                .setWorkerThreads(2000)         //服务工作线程最大数
                .setClientTimeout(1000)         //服务通信read超时
                .setUseZk(true);                //使用zookeeper集群管理服务
        ThriftServer server = new ThriftServer(sc, new UserInfoImpl()); //UserInfoImpl.class 为thrift服务实现类
        //ThriftServer server = new ThriftServer(sc, new UserInfoImpl(), new UserServiceImpl());//多服务实现模式，UserInfoImpl UserServiceImpl 两个是不同thrift服务实现类
        server.start();
    }
	    
    thrift client:
    public static void main(String[] args) throws TException {
        ThriftPoolConfig config = new ThriftPoolConfig();
        config.setZkAddress("10.1.128.212:2181");//zookeeper集群地址
        config.setJdns("/jdns/si/server");//服务端使用的节点
        //config.setClientClass(UserInfo.Client.class, UserService.Client.class);//本地模式时使用，设置client
        config.setConnTotal(10);//client会对每个server创建最多10个连接
        config.setUseZk(true);//使用zookeeper管理client
        //config.setHosts("127.0.0.1:9091"); //本地模式时直连rpc服务
        config.setProtocol(Constant.Protocol.TCOMPACTPROTOCOL);//thrift通信压缩模式
        ThriftClient thriftClient = new ThriftClient(config);
        UserInfo.Iface client = (UserInfo.Iface) thriftClient.getClient(UserInfo.Client.class);//获取thrift客户端
        //UserService.Iface client = (UserService.Iface) thriftClient.getClient(UserService.Client.class);
        int age = client.getAge();
    }