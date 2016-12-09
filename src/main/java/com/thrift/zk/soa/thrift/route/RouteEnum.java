package com.thrift.zk.soa.thrift.route;

/**
 * Author: baichuan - xiajun
 * Date: 2016/12/06 19:37
 */
public enum RouteEnum {
    RANDOM("random"), WEIGHT("weight"), ROTATION("rotation");
    private String route;
    private RpcRoute rpcRoute;

    RouteEnum(String route) {
        this.route = route;
        this.rpcRoute = buildRpcRoute(route);
    }

    public String getValue() {
        return this.route;
    }

    public RpcRoute getRoute() {
        return rpcRoute;
    }

    private RpcRoute buildRpcRoute(String routeEnum) {
        RpcRoute route = new RandomRoute();
        if ("random".equals(routeEnum)) {
            route = new RandomRoute();
        } else if ("weight".equals(routeEnum)) {
            route = new WeightRoute();
        } else if ("rotation".equals(routeEnum)) {
            route = new RotationRoute();
        }
        return route;
    }
}
