package com.zizhizhan.legacies.prototypes.mock;

import com.zizhizhan.legacies.service.HelloClient;
import com.zizhizhan.legacies.service.HelloService;
import org.junit.Test;
import static com.zizhizhan.legacies.prototypes.mock.MockControl.*;

public class MockSystemTests {
	
	@Test
	public void testMockSystem() {
		HelloService hs = createMock(HelloService.class);
		HelloClient hc = new HelloClient();
		hc.setHelloService(hs);
		
		hs.sayHello("");
		expect(hs.echo("Hello")).andReturn("Hello World!");
		expect(hs.echo("Hello")).andReturn("This is message is returned by MockedObject.");
		expect(hs.echo("Hello")).andReturn("MyTest.");
		expect(hs.echo("Hello")).andReturn("Invoke HelloService echo.");
		expect(hs.echo("Hello")).andReturn("Just test for fun.");
		expect(hs.echo("Hello")).andReturn("Test ending.");

		replay(hs);

		hc.exec();

		verify(hs);
	}

}