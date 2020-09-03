package com.zizhizhan.legacy.pattern.filterchain;

import java.io.IOException;

import javax.servlet.Servlet;
import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;

import com.zizhizhan.legacy.pattern.filterchain.servlet.ServletHandler;
import com.zizhizhan.legacy.pattern.filterchain.servlet.AsyncFilter;
import com.zizhizhan.legacy.pattern.filterchain.servlet.LoggingFilter;
import org.easymock.EasyMock;
import org.easymock.IMocksControl;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;

public class ServletHandlerTests {

    private ServletHandler sh;
    private IMocksControl ctrl;
    private Servlet servlet;
    private ServletRequest request;
    private ServletResponse response;

    @Before
    public void setUp() {
        sh = new ServletHandler();
        ctrl = EasyMock.createControl();
        servlet = new Servlet() {

            @Override
            public void service(ServletRequest req, ServletResponse res) throws ServletException, IOException {
                try {
                    Thread.sleep(10000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                System.out.println("Processing...");
            }

            @Override
            public void init(ServletConfig config) throws ServletException {

            }

            @Override
            public String getServletInfo() {
                return null;
            }

            @Override
            public ServletConfig getServletConfig() {

                return null;
            }

            @Override
            public void destroy() {


            }
        };
        request = ctrl.createMock(ServletRequest.class);
        response = ctrl.createMock(ServletResponse.class);
    }

    @Test
    public void execute() throws Exception {
        sh.setServlet(servlet);
        sh.add(new AsyncFilter());
        sh.add(new LoggingFilter());

        ctrl.replay();

        for (int i = 0; i < 10; i++) {
            sh.handle("", request, response);
        }

        Thread.sleep(50000);

        ctrl.verify();
        ctrl.reset();
    }


    @After
    public void tearDown() {
        sh = null;
        ctrl = null;
        servlet = null;
        request = null;
        response = null;
    }
}
