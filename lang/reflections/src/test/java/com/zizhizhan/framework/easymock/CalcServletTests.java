package com.zizhizhan.framework.easymock;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zizhizhan.test.servlet.CalcServlet;
import org.junit.Assert;
import org.junit.Test;
import static org.easymock.EasyMock.*;

public class CalcServletTests {
	
	private final CalcServlet servlet = new CalcServlet();
	
	@Test
	public void testPlus() throws IOException, ServletException{
				
		HttpServletRequest request = createMock(HttpServletRequest.class);
		HttpServletResponse response = createMock(HttpServletResponse.class);
		
		StringWriter out = new StringWriter();
		
		expect(response.getWriter()).andReturn(new PrintWriter(out));
		expect(request.getParameter(isA(String.class))).andReturn("5");	
		expect(request.getParameter(isA(String.class))).andReturn("3");		
		expect(request.getParameter("op")).andReturn("+");		
				
		replay(request, response);
		
		try{		
			servlet.doGet(request, response);			
			Assert.assertEquals("The result is 8\n", out.toString());
		}finally{
			verify(request, response);	
		}
	}

}
