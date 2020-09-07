package com.zizhizhan.aopalliance.rmi.v0;

import java.rmi.Naming;

public class HelloClient {

    public static void main(String[] args) throws Exception {
        String url = "rmi://127.0.0.1/HelloService";
        HelloService hs = (HelloService) Naming.lookup(url);
        hs.sayHello();
        System.out.println(hs.getServiceName());
    }

}