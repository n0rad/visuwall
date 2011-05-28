package net.awired.jwall;

import org.junit.Assert;
import org.junit.Test;

public class StatusTest {

	@Test
	public void should_test() {
		String value = System.getenv("status");
		String valueProp = System.getProperty("status");
		System.out.println("##########" + value);
		System.out.println("##########" + valueProp);
		if (value != null && value.trim().equals("")) {
			System.out.println("status value :" + value);
			if ("fail".equals(value)) {
				Assert.fail("this is a fail project");
			} else if ("error".equals(value)) {
				throw new RuntimeException("This is a Error project");
			}
		}
	}
	
}
