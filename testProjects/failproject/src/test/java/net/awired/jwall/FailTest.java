package net.awired.jwall;

import junit.framework.Assert;

import org.junit.Test;

public class FailTest {

	@Test
	public void should_fail() {
		Assert.fail("this project always fail");
	}
}
