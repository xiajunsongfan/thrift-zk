package com.thrift.zk.soa.thrift.server;

import com.thrift.zk.soa.thrift.route.RouteEnum;
import com.thrift.zk.soa.utils.Constant;

/**
 * Author: baichuan - xiajun
 * Date: 2016/12/09 10:41
 */
public class ServerRegisterInfo {
    private String[] className;//服务类名
    private RouteEnum route;//分流策略
    private Constant.Protocol protocol;//thrift协议类型

    public ServerRegisterInfo() {
    }

    public ServerRegisterInfo(String[] className, RouteEnum route, Constant.Protocol protocol) {
        this.className = className;
        this.route = route;
        this.protocol = protocol;
    }

    public String[] getClassName() {
        return className;
    }

    public void setClassName(String[] className) {
        this.className = className;
    }

    public RouteEnum getRoute() {
        return route;
    }

    public void setRoute(RouteEnum route) {
        this.route = route;
    }

    public Constant.Protocol getProtocol() {
        return protocol;
    }

    public void setProtocol(Constant.Protocol protocol) {
        this.protocol = protocol;
    }
}
