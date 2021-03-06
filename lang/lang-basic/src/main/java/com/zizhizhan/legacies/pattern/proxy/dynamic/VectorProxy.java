package com.zizhizhan.legacies.pattern.proxy.dynamic;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.Vector;

public class VectorProxy implements InvocationHandler {

    public static Object factory(Object obj) {
        Class<?> cls = obj.getClass();
        return Proxy.newProxyInstance(cls.getClassLoader(), cls.getInterfaces(), new VectorProxy(obj));
    }

    private Object target;

    public VectorProxy(Object target) {
        this.target = target;
    }

    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println("\nbefore calling " + method);
        if (args != null) {
            for (int i = 0; i < args.length; i++) {
                System.out.println("args[" + i + "]: " + args[i] + "");
            }
        }

        try {
            return method.invoke(target, args);
        } finally {
            System.out.println("after calling " + method + "\n");
        }
    }

    @SuppressWarnings("unchecked")
    public static void main(String[] args) {
        List<String> list = (List<String>) factory(new Vector<String>(10));
        list.add("Hello");
        list.add("world");
        System.out.println(list.equals(new Object()));

        System.out.println(list.hashCode());
        System.out.println(list);
        System.out.println(list.getClass()); // Object中只有equals, toString, hashcode 会调用invoke

        list.clear();
        System.out.println(list);
    }

}
