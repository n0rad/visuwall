package net.awired.visuwall.cli;

import java.io.IOException;
import java.net.URL;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.Manifest;

import net.awired.ajsl.cli.argument.CliArgumentManager;
import net.awired.ajsl.cli.argument.CliArgumentParseException;
import net.awired.ajsl.cli.argument.args.CliNoParamArgument;
import net.awired.ajsl.cli.argument.args.CliOneParamArgument;
import net.awired.ajsl.cli.param.CliParamInt;
import net.awired.ajsl.cli.param.CliParamString;

class ArgumentManager extends CliArgumentManager {

	public final CliOneParamArgument<Integer> portArg;

	public final CliOneParamArgument<String> contextPath;

	public final CliNoParamArgument version;

	public final CliOneParamArgument<String> rootFolder;

	public ArgumentManager(Main main) {
		super("visuwall");

		CliParamInt portParam = new CliParamInt("port");
		portParam.setNegativable(false);
		portParam.setZeroable(false);

		portArg = new CliOneParamArgument<Integer>('p', portParam);
		portArg.setParamOneDefValue(8081);
		portArg.setName("port");
		portArg.setDescription("Port for servlet Contrainer");
		addArg(portArg);

		contextPath = new CliOneParamArgument<String>('c', new CliParamString(
				"contextPath"));
		contextPath.setParamOneDefValue("/");
		contextPath.setName("contextpath");
		contextPath.setDescription("Context path to access the application");
		addArg(contextPath);

		version = new CliNoParamArgument('V') {
			public void parse(List<String> args)
					throws CliArgumentParseException {

				try {
					String version = null;
					Enumeration<URL> manifests;
					manifests = ArgumentManager.class.getClassLoader()
							.getResources("META-INF/MANIFEST.MF");
					while (manifests.hasMoreElements()) {
						URL res = (URL) manifests.nextElement();
						Manifest manifest = new Manifest(res.openStream());
						version = manifest.getMainAttributes().getValue(
								"VisuwallVersion");
						if (version != null) {
							break;
						}

					}

					System.out.print("Visuwall ");
					System.out.println(version);
					System.exit(0);
				} catch (IOException e) {
					System.out.println("can not found version : "
							+ e.getMessage());
				}

			};
		};
		version.setName("version");
		addArg(version);

		rootFolder = new CliOneParamArgument<String>('r', new CliParamString("home"));
		rootFolder.setParamOneDefValue("~/.visuwall");
		rootFolder.setName("home");
		rootFolder.setDescription("Visuwall root folder");
		addArg(rootFolder);
	}
}