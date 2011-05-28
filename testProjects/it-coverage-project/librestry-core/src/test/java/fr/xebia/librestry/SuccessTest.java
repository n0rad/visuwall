package fr.xebia.librestry;

import junit.framework.Assert;

import org.junit.Test;

import fr.xebia.librestry.domain.Book;


public class SuccessTest {

	@Test
	public void test_success1() {
		Book book = new Book();
		book.setAuthor("author");
		book.setTitle("title");
		
		Assert.assertEquals("author", book.getAuthor());
		Assert.assertEquals("title", book.getTitle());
	}
	
	@Test
	public void test_success2() {
		
	}
	
	@Test
	public void test_success3() {
		
	}
}
