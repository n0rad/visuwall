package fr.xebia.librestry.it;

import javax.ws.rs.core.MultivaluedMap;

import org.junit.Assert;
import org.junit.Test;

import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.core.util.MultivaluedMapImpl;

import fr.xebia.librestry.domain.Book;

public class BookIT {

	@Test
	public void should_create_a_new_book() {
		MultivaluedMap<String, String> form = new MultivaluedMapImpl();
		form.add("title", "Clean Code");
		form.add("author", "Robert C. Martin");

		Client client = new Client();
		WebResource webResource = client.resource(buildUri("/book"));
		Book book = webResource.post(Book.class, form);

		Assert.assertEquals("Clean Code", book.getTitle());
		Assert.assertEquals("Robert C. Martin", book.getAuthor());
	}

	@Test
	public void should_list_3_books() {

	}

	private String buildUri(String uri) {
		return "http://localhost:9876/librestry/api" + uri;
	}

}
