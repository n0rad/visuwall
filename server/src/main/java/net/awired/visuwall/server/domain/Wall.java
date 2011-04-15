package net.awired.visuwall.server.domain;

import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import javax.persistence.CascadeType;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Transient;

import net.awired.visuwall.api.domain.ProjectId;

import org.springframework.util.AutoPopulatingList;

import com.google.common.base.Objects;

@Entity
@NamedQueries({ @NamedQuery(name = Wall.QUERY_NAMES, query = "SELECT wall.name FROM Wall") })
public final class Wall {
	
	public static final String QUERY_NAMES = "wallNames";

	@Id
	private String name;

	@OneToMany(cascade = CascadeType.ALL, fetch = FetchType.EAGER)
	private List<SoftwareAccess> softwareAccesses = new AutoPopulatingList<SoftwareAccess>(
			SoftwareAccess.class);

	public Wall() {
	}

	public Wall(String name) {
		this.name = name;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null || !(obj instanceof Wall))
			return false;
		return name == ((Wall) obj).name;
	}

	@Override
	public String toString() {
		return Objects.toStringHelper(this) //
				.add("name", name) //
				.toString();
	}

	// /////////////////////////////////////////////////////////

}
