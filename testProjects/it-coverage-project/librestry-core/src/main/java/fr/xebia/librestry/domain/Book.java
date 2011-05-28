package fr.xebia.librestry.domain;

import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlRootElement;

import com.google.common.base.Objects;

@XmlAccessorType(XmlAccessType.FIELD)
@XmlRootElement
public class Book {
	private String title;
	private String author;

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this) //
		        .add("title", title) //
		        .add("author", author) //
		        .toString();
	}

	@Override
	public boolean equals(Object o) {
		if (o == null || !(o instanceof Book)) {
			return false;
		}
		return Objects.equal(title, ((Book) o).title);
	}

	@Override
	public int hashCode() {
		return Objects.hashCode(title, author);
	}
}
