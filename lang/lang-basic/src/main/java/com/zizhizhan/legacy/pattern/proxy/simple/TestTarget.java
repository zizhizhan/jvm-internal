package com.zizhizhan.legacy.pattern.proxy.simple;

public class TestTarget implements TestInterface {

	public String doTest1(String str, int num) {
		return str + num;
	}

	public void doTest2(String[] strings) {
		throw new RuntimeException("I don't know what to do in doTest2");
	}

}
