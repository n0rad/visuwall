/**
 *     Copyright (C) 2010 Julien SMADJA <julien dot smadja at gmail dot com> - Arnaud LEMAIRE <alemaire at norad dot fr>
 *
 *     Licensed under the Apache License, Version 2.0 (the "License");
 *     you may not use this file except in compliance with the License.
 *     You may obtain a copy of the License at
 *
 *             http://www.apache.org/licenses/LICENSE-2.0
 *
 *     Unless required by applicable law or agreed to in writing, software
 *     distributed under the License is distributed on an "AS IS" BASIS,
 *     WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *     See the License for the specific language governing permissions and
 *     limitations under the License.
 */

package fr.xebia.librestry.webservice;

import static com.google.common.base.Preconditions.checkState;
import static com.google.common.base.Strings.isNullOrEmpty;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Logger;

import javax.ws.rs.FormParam;
import javax.ws.rs.GET;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import javax.ws.rs.core.Response.Status;

import fr.xebia.librestry.domain.Book;

@Path("/book")
@Produces(MediaType.APPLICATION_XML)
public class BookResource {

	private static final Logger LOG = Logger.getLogger(BookResource.class.getName());

	private static final Map<String, Book> BOOKS = new HashMap<String, Book>();

	@POST
	public Response create(@FormParam("title") String title, @FormParam("author") String author) {
		checkState(!isNullOrEmpty(title), "title parameter is mandatory");
		checkState(!isNullOrEmpty(author), "author parameter is mandatory");

		Book book = new Book();
		book.setTitle(title);
		book.setAuthor(author);

		BOOKS.put(title, book);

		LOG.info("Add " + title + " to your library");

		return Response.status(Status.OK).entity(book).build();
	}

	@GET
	public Response list() {
		Collection<Book> books = BOOKS.values();
		LOG.info("Your library:\n");
		for (Book book : books) {
			LOG.info(book + "\n");
		}
		return Response.status(Status.OK).entity(books).build();
	}

}
