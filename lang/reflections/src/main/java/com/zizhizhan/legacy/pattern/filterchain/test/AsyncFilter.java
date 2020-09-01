package com.zizhizhan.legacy.pattern.filterchain.test;

import java.io.IOException;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.FilterConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;


public class AsyncFilter implements Filter {
	
	private final Executor pool = Executors.newCachedThreadPool();

	@Override
	public void init(FilterConfig filterConfig) throws ServletException {
		
	}

	@Override
	public void doFilter(final ServletRequest request, final ServletResponse response, final FilterChain chain) throws IOException,
			ServletException {	
		pool.execute(new Runnable() {			
			public void run() {				
				try {
					chain.doFilter(request, response);
				} catch (Exception e) {					
					throw new IllegalStateException(e);	
				}			
			}
		});
	}

	@Override
	public void destroy() {
		
	}

}
