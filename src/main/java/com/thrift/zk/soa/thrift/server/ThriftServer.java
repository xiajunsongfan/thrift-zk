
package com.thrift.zk.soa.thrift.server;

import com.thrift.zk.soa.exception.SoaException;
import com.thrift.zk.soa.exception.ThriftUncaughtExceptionHandler;
import com.thrift.zk.soa.thrift.NodeInfo;
import com.thrift.zk.soa.utils.Constant;
import com.thrift.zk.soa.utils.JsonUtil;
import com.thrift.zk.soa.utils.NetworkUtil;
import com.xj.zk.ZkClient;
import org.apache.thrift.TMultiplexedProcessor;
import org.apache.thrift.TProcessor;
import org.apache.thrift.protocol.TBinaryProtocol;
import org.apache.thrift.protocol.TCompactProtocol;
import org.apache.thrift.protocol.TProtocolFactory;
import org.apache.thrift.server.TServer;
import org.apache.thrift.server.TThreadedSelectorServer;
import org.apache.thrift.transport.TFramedTransport;
import org.apache.thrift.transport.TNonblockingServerSocket;
import org.apache.thrift.transport.TTransportException;
import org.apache.thrift.transport.TTransportFactory;
import org.apache.zookeeper.CreateMode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Constructor;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.concurrent.SynchronousQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * Author:  xiajun
 * Date: 16/10/31 22:22
 */
public class ThriftServer {
    private final static Logger LOGGER = LoggerFactory.getLogger(ThriftServer.class);
    private Object[] serverImpls;
    private ZkClient zkClient;
    private TServer server;
    private String serverClassName = "";//服务程序名称
    private ServerConfig sc;//
    private String zkNode;//dns + ip:host
    private boolean multi;//启动模式

    /**
     * 构造函数
     *
     * @param serverImpl thrift 接口服务实现类
     */
    public ThriftServer(ServerConfig sc, Object... serverImpl) {
        if (serverImpl == null || serverImpl.length < 1) {
            throw new NullPointerException("Service impl class is null.");
        }
        this.serverImpls = serverImpl;
        this.sc = sc;
    }

    /**
     * 多Processor模式
     *
     * @return
     * @throws Exception
     */
    private TProcessor createMultiProcessor() throws Exception {
        multi = true;
        TMultiplexedProcessor processors = new TMultiplexedProcessor();
        for (Object serverImpl : serverImpls) {
            Class[] classes = serverImpl.getClass().getInterfaces();//服务实现的接口
            Object impl = serverImpl;
            String processorName = null;
            Class ifaceClass = null;
            for (Class aClass : classes) {
                if (aClass.getName().endsWith("$Iface")) {
                    processorName = aClass.getName().replace("$Iface", "$Processor");
                    String ifaceName = aClass.getName();
                    serverClassName += ifaceName.replace("$Iface", ",");
                    ifaceClass = aClass;
                    break;
                }
            }
            if (processorName == null) {
                Class serverClass = serverImpls[0].getClass();
                throw new IllegalArgumentException(serverClass.getName() + " , not implements " + serverClass.getSimpleName() + ".Iface");
            }
            Class proClazz = Class.forName(processorName);
            Constructor constructor = proClazz.getConstructor(ifaceClass);
            TProcessor processor = (TProcessor) constructor.newInstance(impl);
            processors.registerProcessor(getServerName(ifaceClass), processor);
        }
        serverClassName = serverClassName.substring(0, serverClassName.length() - 1);
        return processors;
    }

    /**
     * 单Processor模式
     *
     * @return
     * @throws Exception
     */
    private TProcessor createProcessor() throws Exception {
        if (serverImpls == null || serverImpls.length < 1) {
            throw new NullPointerException("Service impl class is null.");
        }
        Class[] classes = serverImpls[0].getClass().getInterfaces();//服务实现的接口
        Object impl = serverImpls[0];
        String processorName = null;
        Class ifaceClass = null;
        for (Class aClass : classes) {
            if (aClass.getName().endsWith("$Iface")) {
                processorName = aClass.getName().replace("$Iface", "$Processor");
                String ifaceName = aClass.getName();
                serverClassName += ifaceName.replace("$Iface", "");
                ifaceClass = aClass;
                break;
            }
        }
        if (processorName == null) {
            Class serverClass = serverImpls[0].getClass();
            throw new IllegalArgumentException(serverClass.getName() + " , not implements " + serverClass.getSimpleName() + ".Iface");
        }
        Class proClazz = Class.forName(processorName);
        Constructor constructor = proClazz.getConstructor(ifaceClass);
        TProcessor processor = (TProcessor) constructor.newInstance(impl);
        return processor;
    }

    /**
     * 创建服务
     *
     * @return ThriftServer
     * @throws Exception
     */
    public ThriftServer build() throws Exception {
        TProcessor processor;
        if (serverImpls.length > 1) {
            processor = createMultiProcessor();
        } else {
            processor = createProcessor();
        }
        String host_;
        if (Constant.IP_0.equals(sc.getHost())) {//由于0.0.0.0属于全局ip，所以绑定时不能绑在固定ip上
            sc.setHost(NetworkUtil.getAddress(sc.getHost()));
            host_ = Constant.IP_0;
        } else {
            sc.setHost(NetworkUtil.getAddress(sc.getHost()));
            host_ = sc.getHost();
        }
        createThriftServer(host_, processor);
        return this;
    }

    /**
     * 创建thrift异步服务端
     *
     * @param host
     * @param processor
     * @throws TTransportException
     * @throws UnknownHostException
     */
    private void createThriftServer(String host, TProcessor processor) throws TTransportException, UnknownHostException {
        InetSocketAddress address = new InetSocketAddress(InetAddress.getByName(host), sc.getPort());
        TNonblockingServerSocket socket = new TNonblockingServerSocket(address, sc.getClientTimeout());
        TTransportFactory transportFactory = new TFramedTransport.Factory();
        TProtocolFactory proFactory;
        if (Constant.Protocol.TBINARYPROTOCOL == sc.getProtocol()) {
            proFactory = new TBinaryProtocol.Factory();
        } else {
            proFactory = new TCompactProtocol.Factory();
        }
        if (sc.getWorkerThreadPool() == null) {
            sc.setWorkerThreadPool(new ThreadPoolExecutor(2, sc.getWorkerThreads(), 30000L, TimeUnit.MILLISECONDS, new SynchronousQueue<Runnable>(), new WorkerThreadFactory()));
        }
        TThreadedSelectorServer.Args arg = new TThreadedSelectorServer.Args(socket).processor(processor).selectorThreads(sc.getSelectorThreads())
                .transportFactory(transportFactory).protocolFactory(proFactory).executorService(sc.getWorkerThreadPool());
        arg.maxReadBufferBytes = sc.getMaxReadBufferBytes();
        arg.acceptQueueSizePerThread(sc.getAcceptQueueSizePerThread());//每个选择器上最大支持的队列数，不建议太大，socke连接长时间放在队列中没有必要
        arg.stopTimeoutVal(30);//默认为60s 关闭线程池的等待时间
        server = new TThreadedSelectorServer(arg);
    }

    /**
     * 启动thrift服务
     */
    public void start() throws Exception {
        build();
        Thread.setDefaultUncaughtExceptionHandler(new ThriftUncaughtExceptionHandler());
        Thread startThread = new Thread(new Runnable() {
            @Override
            public void run() {
                server.serve();
            }
        });
        startThread.start();
        Runtime.getRuntime().addShutdownHook(new Thread(new Runnable() {//系统停止时服务关闭
            @Override
            public void run() {
                stop();
            }
        }));
        for (int i = 0; i < 10; i++) {
            if (server.isServing()) {
                try {
                    if (sc.isUseZk()) {
                        addZkNode();
                    }
                    System.out.println("***********************************************************");
                    System.out.println("*                                                         *");
                    System.out.println("* Server start success, ServerName: " + serverImpls[0].getClass().getSimpleName());
                    System.out.println("* Host: " + sc.getHost() + ", Port: " + sc.getPort() + ", Multi: " + multi);
                    System.out.println("* Uszk: " + sc.isUseZk() + ", Dns: " + sc.getDns());
                    System.out.println("*                                                         *");
                    System.out.println("***********************************************************");
                } catch (Exception e) {
                    stop();
                    LOGGER.error("Zookeeper {} connection error.", sc.getZkAddress(), e);
                }
                break;
            } else {
                System.out.println("Server starting ......");
            }
            try {
                Thread.sleep(1000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        if (!server.isServing()) {
            throw new SoaException("Server start faill.");
        }
    }

    /**
     * 向zookeeper中添加本服务节点
     *
     * @throws Exception
     */
    void addZkNode() throws Exception {
        if (sc.getDns() != null && sc.getZkAddress() != null) {
            zkClient = new ZkClient(sc.getZkAddress(), sc.getZkSessionTimeout(), sc.getZkConnTimeout());
            if (!zkClient.exists(sc.getDns())) {
                zkClient.create(sc.getDns(), CreateMode.PERSISTENT);
            }
            zkNode = sc.getDns() + "/" + sc.getHost() + ":" + sc.getPort();
            if (zkClient.exists(zkNode)) {
                server.stop();
                throw new IllegalStateException("Zookeeper node [" + zkNode + "] has been.");
            }
            NodeInfo si = new NodeInfo(sc.getHost(), sc.getPort(), sc.getCluster());
            si.setWeight(sc.getWeight());
            zkClient.create(zkNode, JsonUtil.toString(si).getBytes(Charset.forName("UTF-8")), true);
            ServerRegisterInfo sri = new ServerRegisterInfo(serverClassName, sc.getRoute(), sc.getProtocol());
            zkClient.setData(sc.getDns(), JsonUtil.toString(sri).getBytes(Charset.forName("UTF-8")));
        } else {
            LOGGER.error("Server is not registered to zookeeper.");
            throw new IllegalArgumentException("Using zookeeper[Zk = true], there is no configuration addressuse[dns = null or zkaddress = null].");
        }
    }

    /**
     * 获取服务名称
     *
     * @param clazz
     * @return
     */
    private String getServerName(Class clazz) {
        String[] packages = clazz.getCanonicalName().split("\\.");
        if (packages.length > 1) {
            return packages[packages.length - 2];
        }
        return null;
    }

    /**
     * 停止服务
     */
    public void stop() {
        String sinfo = zkNode;
        if (zkNode == null) {
            sinfo = sc.getHost() + ":" + sc.getPort();
        }
        System.out.println("Server stopping [" + sinfo + "] ......");
        if (server == null || !server.isServing()) {
            LOGGER.warn("Server has not started.");
        }
        if (sc.getDns() != null && zkClient != null) {
            try {
                zkClient.delete(zkNode);
                zkClient.close();
                Thread.sleep(2000);
            } catch (Exception e) {
                LOGGER.error("Zookeeper zkClient close error.", e);
            }
        }
        server.stop();
        LOGGER.info("Server stop success. ServerName [" + serverImpls[0].getClass().getSimpleName() + "].");
    }
}
