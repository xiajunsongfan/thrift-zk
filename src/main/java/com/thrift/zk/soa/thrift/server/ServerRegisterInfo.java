package com.thrift.zk.soa.thrift.server;

import com.thrift.zk.soa.thrift.route.RouteEnum;

/**
 * Author: baichuan - xiajun
 * Date: 2016/12/09 10:41
 */
public class ServerRegisterInfo {
    private String className;
    private RouteEnum route;

    public ServerRegisterInfo() {
    }

    public ServerRegisterInfo(String className, RouteEnum route) {
        this.className = className;
        this.route = route;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public RouteEnum getRoute() {
        return route;
    }

    public void setRoute(RouteEnum route) {
        this.route = route;
    }
}
