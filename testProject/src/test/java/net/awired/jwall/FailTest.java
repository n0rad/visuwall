package net.awired.jwall;

import net.awired.jwall.testproject.TestProject;

import org.junit.Assert;
import org.junit.Test;

public class FailTest {

	@Test
	public void should_fail() {
		TestProject.main(null);
	}
}
