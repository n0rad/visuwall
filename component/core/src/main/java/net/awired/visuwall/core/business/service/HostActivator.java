package net.awired.visuwall.core.business.service;
 
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleActivator;
import org.osgi.framework.BundleContext;

public class HostActivator implements BundleActivator {
	private BundleContext context = null;

	public HostActivator() {
	}

	public void start(BundleContext context) {
		this.context = context;
	}

	public void stop(BundleContext context) {
		this.context = null;
	}

	public Bundle[] getBundles() {
		if (context != null) {
			return context.getBundles();
		}
		return null;
	}

	public BundleContext getContext() {
		return context;
	}
}