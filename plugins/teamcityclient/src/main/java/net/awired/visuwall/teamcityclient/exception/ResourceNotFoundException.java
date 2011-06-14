package net.awired.visuwall.teamcityclient.exception;

import com.sun.jersey.api.client.UniformInterfaceException;

public class ResourceNotFoundException extends Exception {

	private static final long serialVersionUID = -4304928156912802898L;

	public ResourceNotFoundException(String msg) {
		super(msg);
	}

	public ResourceNotFoundException(UniformInterfaceException e) {
		super(e);
	}
}
