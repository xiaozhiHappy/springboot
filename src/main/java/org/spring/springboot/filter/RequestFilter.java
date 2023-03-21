package org.spring.springboot.filter;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

public class RequestFilter implements Filter{
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain filterChain)
			throws IOException, ServletException {
		long currentTime = System.currentTimeMillis();
		filterChain.doFilter(request, response);
		System.out.println("execute end ====》" +  (System.currentTimeMillis() - currentTime));
	}

}
