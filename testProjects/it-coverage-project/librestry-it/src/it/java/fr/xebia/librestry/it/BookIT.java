/**
 * Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

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
