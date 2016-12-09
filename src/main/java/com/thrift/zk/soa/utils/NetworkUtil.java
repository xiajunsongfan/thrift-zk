
package com.thrift.zk.soa.utils;

import java.net.Inet4Address;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

/**
 * Author: xiajun
 * Date: 2016-10-20
 * Time: 09:26:00
 *
 */
public class NetworkUtil {
    private final static String IP_0 = "0.0.0.0";

    /**
     * 获取本机ip地址
     *
     * @return
     * @throws SocketException
     */
    public static String getAddress() throws SocketException {
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        String address = null;
        while (e.hasMoreElements()) {
            List<InterfaceAddress> lsit = e.nextElement().getInterfaceAddresses();
            for (InterfaceAddress interfaceAddress : lsit) {
                if (interfaceAddress.getAddress() instanceof Inet4Address) {
                    String host = interfaceAddress.getAddress().getHostAddress();
                    if (host.startsWith("192.168") || host.startsWith("10.13")) {
                        address = host;
                    } else if (host.equals("127.0.0.1")) {
                        continue;
                    } else {
                        return host;
                    }
                }
            }
        }
        return address;
    }

    /**
     * 获取本机ip地址，按照给定ip或前缀
     *
     * @param ip 172.17 or 172.17.11.24
     * @return
     * @throws SocketException
     */
    public static String getAddress(String ip) throws SocketException {
        if (ip == null || IP_0.equals(ip.trim()) || "".equals(ip.trim())) {
            return getAddress();
        }
        Enumeration<NetworkInterface> e = NetworkInterface.getNetworkInterfaces();
        while (e.hasMoreElements()) {
            List<InterfaceAddress> lsit = e.nextElement().getInterfaceAddresses();
            for (InterfaceAddress interfaceAddress : lsit) {
                if (interfaceAddress.getAddress() instanceof Inet4Address) {
                    String host = interfaceAddress.getAddress().getHostAddress();
                    if (host.startsWith(ip)) {
                        return host;
                    }
                }
            }
        }
        return null;
    }
}
