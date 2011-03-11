package net.awired.jwall;

import org.junit.Test;

public class ErrorTest {

	@Test
	public void should_error() {
		throw new RuntimeException("this project always do an error on tests");
	}
}
