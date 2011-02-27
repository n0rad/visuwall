package net.awired.jwall.testproject;

import org.junit.Assert;

public class TestProject {
	public static void main(String[] args) {
		String value = System.getProperty("status");
		if (value != null) {
			if ("fail".equals(value)) {
				Assert.fail("this is a fail project");
			} else {
				throw new RuntimeException("This is a Error project");
			}
		}
	}
}
