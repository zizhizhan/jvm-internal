package com.zizhizhan.aopalliance.aop;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Method;
import java.util.List;

import org.aopalliance.intercept.MethodInterceptor;
import org.aopalliance.intercept.MethodInvocation;

public class ReflectiveMethodInvocation implements MethodInvocation {

	private final Object proxy;

	private final Object target;

	private final Method method;

	private Object[] arguments;

	private final Class<?> targetClass;

	private final List<?> interceptorsAndDynamicMethodMatchers;

	private int currentInterceptorIndex = -1;

	public ReflectiveMethodInvocation(Object proxy, Object target, Method method, Object[] arguments, Class<?> targetClass, List<?> interceptorsAndDynamicMethodMatchers) {
		super();
		this.proxy = proxy;
		this.target = target;
		this.method = method;
		this.arguments = arguments;
		this.targetClass = targetClass;
		this.interceptorsAndDynamicMethodMatchers = interceptorsAndDynamicMethodMatchers;
	}

	public final Object getProxy() {
		return this.proxy;
	}

	public final Object getThis() {
		return this.target;
	}

	public final AccessibleObject getStaticPart() {
		return this.method;
	}

	public final Method getMethod() {
		return this.method;
	}

	public final Object[] getArguments() {
		return (this.arguments != null ? this.arguments : new Object[0]);
	}

	public void setArguments(Object[] arguments) {
		this.arguments = arguments;
	}

	@Override
	public Object proceed() throws Throwable {
		// We start with an index of -1 and increment early.
		if (currentInterceptorIndex == interceptorsAndDynamicMethodMatchers.size() - 1) {
			return method.invoke(target, arguments);
		}

		Object interceptorOrInterceptionAdvice = interceptorsAndDynamicMethodMatchers.get(++currentInterceptorIndex);
		if (interceptorOrInterceptionAdvice instanceof InterceptorAndDynamicMethodMatcher) {
			// Evaluate dynamic method matcher here: static part will already
			// have been evaluated and found to match.
			InterceptorAndDynamicMethodMatcher dm = (InterceptorAndDynamicMethodMatcher) interceptorOrInterceptionAdvice;
			if (dm.methodMatcher.matches(method, targetClass, arguments)) {
				return dm.interceptor.invoke(this);
			} else {
				// Dynamic matching failed. Skip this interceptor and invoke the
				// next in the chain.
				return proceed();
			}
		} else {
			// It's an interceptor, so we just invoke it: The pointcut will have
			// been evaluated statically before this object was constructed.
			return ((MethodInterceptor) interceptorOrInterceptionAdvice).invoke(this);
		}
	}

}
