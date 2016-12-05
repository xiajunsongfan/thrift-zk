package com.mogujie.recsys.soa.idl;

import org.apache.log4j.Logger;
import org.apache.thrift.TException;

import java.util.ArrayList;
import java.util.List;
import java.util.Random;

/**
 * Author: xiajun
 * Date: 2014-08-04
 * Time: 16:41:00
 */
public class UserInfoImpl implements UserInfo.Iface {
    private final Logger logger = Logger.getLogger(UserInfoImpl.class);
    static UserInfo.Client c;
    Random random = new Random();

    @Override
    public int getAge(int age) throws TException {
        int s = 0;
        try {
            s = random.nextInt(500);
            Thread.sleep(s);
        } catch (InterruptedException e) {

        }
        System.out.println(age + "--------------->" + s);
        return s;
    }

    @Override
    public List<User> getUser() throws TException {
        System.out.println("getUser------------------------>");
        List<User> list = new ArrayList<User>();
        User user = new User();
        user.setId(1).setAge(12).setName("zs");
        list.add(user);
        return list;
    }
}
