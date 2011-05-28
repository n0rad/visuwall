package net.awired.jwall;

import org.junit.Assert;
import org.junit.Test;

public class StatusTest {

	@Test
	public void main(String[] args) {
		String value = System.getenv("status");
		if (value != null) {
			System.out.println("status value :" + value);
			if ("fail".equals(value)) {
				Assert.fail("this is a fail project");
			} else if ("error".equals(value)) {
				throw new RuntimeException("This is a Error project");
			}
		}
	}
	
}
