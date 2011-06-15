package net.awired.visuwall.common.client;

import com.google.common.base.Preconditions;
import com.sun.jersey.api.client.Client;
import com.sun.jersey.api.client.UniformInterfaceException;
import com.sun.jersey.api.client.WebResource;
import com.sun.jersey.api.client.config.ClientConfig;
import com.sun.jersey.api.client.config.DefaultClientConfig;

public class GenericSoftwareClient {

	private Client client;

	public GenericSoftwareClient(Client client) {
		Preconditions.checkNotNull(client, "client is mandatory");
		this.client = client;
	}

	public GenericSoftwareClient() {
		ClientConfig clientConfig = new DefaultClientConfig();
		clientConfig.getClasses();
		client = Client.create(clientConfig);
	}

	public <T> T resource(String url, Class<T> clazz) throws ResourceNotFoundException {
		Preconditions.checkNotNull(url, "url is mandatory");
		try {
			WebResource resource = client.resource(url);
			return resource.get(clazz);
		} catch (UniformInterfaceException e) {
			throw new ResourceNotFoundException(e);
		}
	}

}
