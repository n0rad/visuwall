package net.awired.jwall.cli;


import java.net.URL;
import java.security.ProtectionDomain;

import org.mortbay.jetty.Connector;
import org.mortbay.jetty.Server;
import org.mortbay.jetty.bio.SocketConnector;
import org.mortbay.jetty.webapp.WebAppContext;

import net.awired.ajsl.cli.argument.CliArgumentManager;
import net.awired.ajsl.cli.argument.args.CliOneParamArgument;
import net.awired.ajsl.cli.param.CliParamInt;

public class Main {
	
	class ArgumentManager extends CliArgumentManager {
		public final CliOneParamArgument<Integer> intArgument;
		
		public ArgumentManager() {
			super("runnablejar");
			intArgument = new CliOneParamArgument<Integer>('i', new  CliParamInt("value"));
			intArgument.setParamOneDefValue(42);
			addArg(intArgument);
		}
	}
	
	public static void main(String[] args) {
		new Main(args);
	}
	
	public Main(String[] args) {
		ArgumentManager argManager = new ArgumentManager();
		argManager.parse(args);
		System.out.println("argument i is : " + argManager.intArgument.getParamOneValue());
		
		
		Server server = new Server();
		SocketConnector connector = new SocketConnector();

		// Set some timeout options to make debugging easier.
		connector.setMaxIdleTime(1000 * 60 * 60);
		connector.setSoLingerTime(-1);
		connector.setPort(8080);
		server.setConnectors(new Connector[] { connector });

		WebAppContext context = new WebAppContext();
		context.setServer(server);
		context.setContextPath("/");

		ProtectionDomain protectionDomain = Main.class.getProtectionDomain();
		URL location = protectionDomain.getCodeSource().getLocation();
		System.out.println("Starting to load :"  + location);
		context.setDescriptor(location.toExternalForm() + "/WEB-INF/web.xml");
		context.setWar(location.toExternalForm());

		server.addHandler(context);
		try {
			server.start();
			System.in.read();
			server.stop();
			server.join();
		} catch (Exception e) {
			e.printStackTrace();
			System.exit(100);
		}
	}
}
