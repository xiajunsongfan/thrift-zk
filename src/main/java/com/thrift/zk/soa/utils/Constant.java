/*
 * Copyright (c) 2016. mogujie
 */

package com.thrift.zk.soa.utils;

/**
 * author: baichuan - xiajun
 * Date: 2016-08-09
 * Time: 15-36-00
 */
public class Constant {
    public final static String CHARTSET = "utf-8";
    public final static String IP_0 = "0.0.0.0";
    public final static String DEFAULT_CLUSTER = "default-cluster";

    public enum Protocol {
        TBINARYPROTOCOL("TBinaryProtocol"), TCOMPACTPROTOCOL("TCompactProtocol");
        String protocol;

        Protocol(String protocol) {
            this.protocol = protocol;
        }

        public String getProtocol() {
            return this.protocol;
        }
    }
}
