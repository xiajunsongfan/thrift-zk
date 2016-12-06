package com.thrift.zk.soa.thrift.route;

/**
 * Author: baichuan - xiajun
 * Date: 2016/12/06 19:37
 */
public enum RouteEnum {
    RANDOM("random"), WEIGHT("weight"), ROTATION("rotation");
    private String route;

    RouteEnum(String route) {
        this.route = route;
    }

    public String getValue() {
        return this.route;
    }
}
