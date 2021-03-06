package com.zizhizhan.framework.mock;

import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.zizhizhan.test.servlet.CalcServlet;
import org.junit.Test;
import static com.zizhizhan.framework.mock.MockControl.*;

public class CalcServletTests {
	
	private final CalcServlet servlet = new CalcServlet();
	
	@Test
	public void testPlus() throws IOException, ServletException{
		HttpServletRequest request = createMock(HttpServletRequest.class);
		HttpServletResponse response = createMock(HttpServletResponse.class);
		
		StringWriter out = new StringWriter();
		
		expect(request.getParameter("operand1")).andReturn("5");
		expect(request.getParameter("operand2")).andReturn("3");
		expect(request.getParameter("op")).andReturn("*");		
		expect(response.getWriter()).andReturn(new PrintWriter(out));
		
		replay(request, response);
		
		try{		
			servlet.doGet(request, response);			
			System.out.println(out);
		}finally{
			verify(request, response);		
		}
	}

}
