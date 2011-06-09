package net.awired.visuwall.hudsonclient.exception;

import com.sun.jersey.api.client.UniformInterfaceException;

public class ResourceNotFoundException extends Exception {

    private static final long serialVersionUID = -7652064072323674187L;

    public ResourceNotFoundException(String msg) {
        super(msg);
    }

	public ResourceNotFoundException(UniformInterfaceException e) {
		super(e);
	}

}
